package com.accounting.controller;

import com.accounting.constants.OfferStatusEnum;
import com.accounting.dto.client.ClientShortDTO;
import com.accounting.dto.offer.OfferDTO;
import com.accounting.dto.offer.OfferForm;
import com.accounting.dto.offer.OfferPatchDTO;
import com.accounting.dto.offer.OfferUpdateStatusDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.pagination.PaginationDTO;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.exeption.OfferNotFoundException;
import com.accounting.service.offers.OfferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfferController.class)
class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OfferService offerService;

    private OfferDTO testOfferDTO;
    private OfferForm testOfferForm;
    private OfferPatchDTO testOfferPatchDTO;
    private OfferUpdateStatusDTO testOfferUpdateStatusDTO;
    private PageDTO pageDTO;

    @BeforeEach
    void setUp() {
        ClientShortDTO clientShortDTO = new ClientShortDTO();
        clientShortDTO.setClientId(1L);
        clientShortDTO.setFullName("John Doe");
        clientShortDTO.setTelephone("1234567890");

        testOfferDTO = new OfferDTO();
        testOfferDTO.setOfferId(1L);
        testOfferDTO.setClient(clientShortDTO);
        testOfferDTO.setAdultsNo(2);
        testOfferDTO.setChildrenNo(1);
        testOfferDTO.setDestination("Paris");
        testOfferDTO.setPeriod("Summer 2025");
        testOfferDTO.setCurrency("EUR");
        testOfferDTO.setBudget(1000.0);
        testOfferDTO.setGrossPrice(1200.0);
        testOfferDTO.setAdvance(300.0);
        testOfferDTO.setCommission(100.0);
        testOfferDTO.setAcquisitionPrice(1100.0);
        testOfferDTO.setStatus(OfferStatusEnum.OFERTAT);

        testOfferForm = new OfferForm();
        testOfferForm.setClientId(1L);
        testOfferForm.setOfferDate(LocalDate.now());
        testOfferForm.setAdultsNo(2);
        testOfferForm.setChildrenNo(1);
        testOfferForm.setDestination("Paris");
        testOfferForm.setPeriod("Summer 2025");
        testOfferForm.setBudget(1000.0);
        testOfferForm.setGrossPrice(1200.0);
        testOfferForm.setAdvance(300.0);
        testOfferForm.setCommission(100.0);
        testOfferForm.setAcquisitionPrice(1100.0);

        testOfferPatchDTO = new OfferPatchDTO();
        testOfferPatchDTO.setDestination("Rome");
        testOfferPatchDTO.setGrossPrice(1300.0);

        testOfferUpdateStatusDTO = new OfferUpdateStatusDTO();
        testOfferUpdateStatusDTO.setStatus("ACCEPTAT");

        PaginationDTO pagination = new PaginationDTO(1L, 5, List.of(5, 10, 20), 0);
        pageDTO = new PageDTO<>(List.of(testOfferDTO), pagination);
    }

    @Test
    void testGetOfferById_Success() throws Exception {
        // Arrange
        when(offerService.getOfferDTOById(1L)).thenReturn(testOfferDTO);

        // Act & Assert
        mockMvc.perform(get("/offer/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.offerId").value(1))
            .andExpect(jsonPath("$.destination").value("Paris"))
            .andExpect(jsonPath("$.client.fullName").value("John Doe"));

        verify(offerService, times(1)).getOfferDTOById(1L);
    }

    @Test
    void testGetOfferById_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(offerService.getOfferDTOById(999L))
            .thenThrow(new EntityNotFoundException("Offer not found"));

        // Act & Assert
        mockMvc.perform(get("/offer/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Offer not found"));

        verify(offerService, times(1)).getOfferDTOById(999L);
    }

    @Test
    void testGetOffers_WithoutFilter_Success() throws Exception {
        // Arrange
        when(offerService.getOffers(isNull(), eq(0), eq(5), Boolean.TRUE)).thenReturn(pageDTO);

        // Act & Assert
        mockMvc.perform(get("/offer")
                .param("page", "0")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.items[0].destination").value("Paris"))
            .andExpect(jsonPath("$.pagination.totalElements").value(1));

        verify(offerService, times(1)).getOffers(isNull(), eq(0), eq(5), Boolean.TRUE);
    }

    @Test
    void testGetOffers_WithFilter_Success() throws Exception {
        // Arrange
        when(offerService.getOffers(eq("John"), eq(0), eq(10), Boolean.TRUE)).thenReturn(pageDTO);

        // Act & Assert
        mockMvc.perform(get("/offer")
                .param("input", "John")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items", hasSize(1)));

        verify(offerService, times(1)).getOffers(eq("John"), eq(0), eq(10), Boolean.TRUE);
    }

    @Test
    void testGetOffers_DefaultPagination_Success() throws Exception {
        // Arrange
        when(offerService.getOffers(isNull(), eq(0), eq(5), Boolean.TRUE)).thenReturn(pageDTO);

        // Act & Assert - Should use default page=0, size=5
        mockMvc.perform(get("/offer"))
            .andExpect(status().isOk());

        verify(offerService, times(1)).getOffers(isNull(), eq(0), eq(5), Boolean.TRUE);
    }

    @Test
    void testSaveOffer_ValidInput_Success() throws Exception {
        // Arrange
        when(offerService.createOffer(any(OfferForm.class))).thenReturn(testOfferDTO);

        // Act & Assert
        mockMvc.perform(post("/offer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOfferForm)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.offerId").value(1))
            .andExpect(jsonPath("$.destination").value("Paris"));

        verify(offerService, times(1)).createOffer(any(OfferForm.class));
    }

    @Test
    void testSaveOffer_InvalidInput_ReturnsBadRequest() throws Exception {
        // Arrange - Create invalid form (missing required fields)
        OfferForm invalidForm = new OfferForm();
        // clientId and destination are required but not set

        // Act & Assert
        mockMvc.perform(post("/offer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidForm)))
            .andExpect(status().isBadRequest());

        verify(offerService, never()).createOffer(any());
    }

    @Test
    void testSaveOffer_ClientNotFound_ReturnsBadRequest() throws Exception {
        // Arrange
        when(offerService.createOffer(any(OfferForm.class)))
            .thenThrow(new ClientNotFoundException("Client not found with ID: 999"));

        // Act & Assert
        mockMvc.perform(post("/offer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOfferForm)))
            .andExpect(status().isBadRequest());

        verify(offerService, times(1)).createOffer(any(OfferForm.class));
    }

    @Test
    void testSaveOffer_FutureOfferDate_ReturnsBadRequest() throws Exception {
        // Arrange
        testOfferForm.setOfferDate(LocalDate.now().plusDays(5));

        // Act & Assert
        mockMvc.perform(post("/offer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOfferForm)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());

        verify(offerService, never()).createOffer(any());
    }

    @Test
    void testSaveOffer_NegativeBudget_ReturnsBadRequest() throws Exception {
        // Arrange
        testOfferForm.setBudget(-100.0);

        // Act & Assert
        mockMvc.perform(post("/offer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOfferForm)))
            .andExpect(status().isBadRequest());

        verify(offerService, never()).createOffer(any());
    }

    @Test
    void testPatchOffer_Success() throws Exception {
        // Arrange
        when(offerService.patchOffer(eq(1L), any(OfferPatchDTO.class))).thenReturn(testOfferDTO);

        // Act & Assert
        mockMvc.perform(put("/offer/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOfferPatchDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.offerId").value(1));

        verify(offerService, times(1)).patchOffer(eq(1L), any(OfferPatchDTO.class));
    }

    @Test
    void testPatchOffer_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(offerService.patchOffer(eq(999L), any(OfferPatchDTO.class)))
            .thenThrow(new OfferNotFoundException("Offer not found with ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/offer/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOfferPatchDTO)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Offer not found"));

        verify(offerService, times(1)).patchOffer(eq(999L), any(OfferPatchDTO.class));
    }

    @Test
    void testPatchOffer_InvalidInput_ReturnsBadRequest() throws Exception {
        // Arrange
        testOfferPatchDTO.setAdultsNo(100); // Exceeds max of 50

        // Act & Assert
        mockMvc.perform(put("/offer/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOfferPatchDTO)))
            .andExpect(status().isBadRequest());

        verify(offerService, never()).patchOffer(anyLong(), any());
    }

    @Test
    void testPatchOffer_ValidationError_ReturnsBadRequest() throws Exception {
        // Arrange
        when(offerService.patchOffer(eq(1L), any(OfferPatchDTO.class)))
            .thenThrow(new IllegalArgumentException("Invalid data"));

        // Act & Assert
        mockMvc.perform(put("/offer/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOfferPatchDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid data"));
    }

    @Test
    void testUpdateOfferStatus_Success() throws Exception {
        // Arrange
        doNothing().when(offerService).updateOfferStatus(eq(1L), any(OfferUpdateStatusDTO.class));

        // Act & Assert
        mockMvc.perform(patch("/offer/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOfferUpdateStatusDTO)))
            .andExpect(status().isNoContent());

        verify(offerService, times(1)).updateOfferStatus(eq(1L), any(OfferUpdateStatusDTO.class));
    }

    @Test
    void testUpdateOfferStatus_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        doThrow(new OfferNotFoundException("Offer not found with ID: 999"))
            .when(offerService).updateOfferStatus(eq(999L), any(OfferUpdateStatusDTO.class));

        // Act & Assert
        mockMvc.perform(patch("/offer/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOfferUpdateStatusDTO)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Offer not found"));

        verify(offerService, times(1)).updateOfferStatus(eq(999L), any(OfferUpdateStatusDTO.class));
    }

    @Test
    void testUpdateOfferStatus_ValidationError_ReturnsBadRequest() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Invalid status"))
            .when(offerService).updateOfferStatus(eq(1L), any(OfferUpdateStatusDTO.class));

        // Act & Assert
        mockMvc.perform(patch("/offer/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOfferUpdateStatusDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid status"));
    }

    @Test
    void testUpdateOfferStatus_ClientNotFound_ReturnsBadRequest() throws Exception {
        // Arrange
        doThrow(new ClientNotFoundException("Client not found"))
            .when(offerService).updateOfferStatus(eq(1L), any(OfferUpdateStatusDTO.class));

        // Act & Assert
        mockMvc.perform(patch("/offer/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOfferUpdateStatusDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Client not found"));
    }

    @Test
    void testDeleteOffer_Success() throws Exception {
        // Arrange
        doNothing().when(offerService).deleteOfferById(1L);

        // Act & Assert
        mockMvc.perform(delete("/offer/1"))
            .andExpect(status().isNoContent());

        verify(offerService, times(1)).deleteOfferById(1L);
    }

    @Test
    void testDeleteOffer_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        doThrow(new EntityNotFoundException("Offer not found"))
            .when(offerService).deleteOfferById(999L);

        // Act & Assert
        mockMvc.perform(delete("/offer/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Offer not found"));

        verify(offerService, times(1)).deleteOfferById(999L);
    }
}
