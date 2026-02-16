package com.accounting.controller;

import com.accounting.dto.client.ClientShortDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.pagination.PaginationDTO;
import com.accounting.dto.provider.ProviderDTO;
import com.accounting.dto.reservation.ReservationDTO;
import com.accounting.dto.reservation.ReservationForm;
import com.accounting.dto.reservation.ReservationParticipantDTO;
import com.accounting.dto.reservation.ReservationPatchDTO;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.service.reservations.ReservationService;
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
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    private ReservationDTO testReservationDTO;
    private ReservationForm testReservationForm;
    private ReservationPatchDTO testReservationPatchDTO;
    private PageDTO pageDTO;

    @BeforeEach
    void setUp() {
        ClientShortDTO clientShortDTO = new ClientShortDTO();
        clientShortDTO.setClientId(1L);
        clientShortDTO.setFullName("John Doe");
        clientShortDTO.setTelephone("1234567890");

        ProviderDTO providerDTO = new ProviderDTO();
        providerDTO.setProviderId(1L);
        providerDTO.setName("Travel Agency Inc");

        testReservationDTO = new ReservationDTO();
        testReservationDTO.setReservationId(1L);
        testReservationDTO.setOfferId(1L);
        testReservationDTO.setClient(clientShortDTO);
        testReservationDTO.setBookingRef("BK001");
        testReservationDTO.setBookedDate(LocalDate.now());
        testReservationDTO.setDepartureDate(LocalDate.now().plusDays(30));
        testReservationDTO.setReturnDate(LocalDate.now().plusDays(37));
        testReservationDTO.setDestination("Paris");
        testReservationDTO.setHotel("Hotel Paris");
        testReservationDTO.setRoomNo(2);
        testReservationDTO.setTransportType("Flight");
        testReservationDTO.setPrice(1200.0);
        testReservationDTO.setReceipted(300.0);
        testReservationDTO.setBalance(900.0);
        testReservationDTO.setBalanceDueDate(LocalDate.now().plusDays(15));
        testReservationDTO.setCurrency("EUR");
        testReservationDTO.setProvider(providerDTO);
        testReservationDTO.setParticipants(List.of("John Doe", "Jane Doe"));

        ReservationParticipantDTO participant1 = new ReservationParticipantDTO();
        participant1.setParticipantName("John Doe");
        participant1.setParticipantAge(35L);

        ReservationParticipantDTO participant2 = new ReservationParticipantDTO();
        participant2.setParticipantName("Jane Doe");
        participant2.setParticipantAge(10L);

        testReservationForm = new ReservationForm();
        testReservationForm.setClientId(1L);
        testReservationForm.setBookedDate(LocalDate.now());
        testReservationForm.setParticipants(List.of(participant1, participant2));
        testReservationForm.setDepartureDate(LocalDate.now().plusDays(30));
        testReservationForm.setReturnDate(LocalDate.now().plusDays(37));
        testReservationForm.setRoomNo(2);
        testReservationForm.setDestination("Paris");
        testReservationForm.setHotel("Hotel Paris");
        testReservationForm.setTransport("Flight");
        testReservationForm.setTotalPrice(1200.0);
        testReservationForm.setAdvance(300.0);
        testReservationForm.setRemainingCost(900.0);
        testReservationForm.setCurrency("EUR");
        testReservationForm.setPaymentDeadlineDate(LocalDate.now().plusDays(15));
        testReservationForm.setProviderId(1L);

        testReservationPatchDTO = new ReservationPatchDTO();
        testReservationPatchDTO.setDestination("Rome");
        testReservationPatchDTO.setPrice(1300.0);

        PaginationDTO pagination = new PaginationDTO(1L, 5, List.of(5, 10, 20), 0);
        pageDTO = new PageDTO<>(List.of(testReservationDTO), pagination);
    }

    @Test
    void testGetReservationsWithPagination_WithoutClientId_Success() throws Exception {
        // Arrange
        when(reservationService.getReservations(isNull(), eq(0), eq(5))).thenReturn(pageDTO);

        // Act & Assert
        mockMvc.perform(get("/reservation")
                .param("page", "0")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(1)))
            .andExpect(jsonPath("$.data[0].destination").value("Paris"))
            .andExpect(jsonPath("$.pagination.totalElements").value(1));

        verify(reservationService, times(1)).getReservations(isNull(), eq(0), eq(5));
    }

    @Test
    void testGetReservationsWithPagination_WithClientId_Success() throws Exception {
        // Arrange
        when(reservationService.getReservations(eq(1), eq(0), eq(10))).thenReturn(pageDTO);

        // Act & Assert
        mockMvc.perform(get("/reservation")
                .param("clientId", "1")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(1)));

        verify(reservationService, times(1)).getReservations(eq(1), eq(0), eq(10));
    }

    @Test
    void testGetReservationsWithPagination_DefaultPagination_Success() throws Exception {
        // Arrange
        when(reservationService.getReservations(isNull(), eq(0), eq(5))).thenReturn(pageDTO);

        // Act & Assert - Should use default page=0, size=5
        mockMvc.perform(get("/reservation"))
            .andExpect(status().isOk());

        verify(reservationService, times(1)).getReservations(isNull(), eq(0), eq(5));
    }

    @Test
    void testGetReservation_Success() throws Exception {
        // Arrange
        when(reservationService.getReservation(1L)).thenReturn(testReservationDTO);

        // Act & Assert
        mockMvc.perform(get("/reservation/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservationId").value(1))
            .andExpect(jsonPath("$.destination").value("Paris"))
            .andExpect(jsonPath("$.bookingRef").value("BK001"));

        verify(reservationService, times(1)).getReservation(1L);
    }

    @Test
    void testGetReservation_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(reservationService.getReservation(999L))
            .thenThrow(new EntityNotFoundException("Reservation not found"));

        // Act & Assert
        mockMvc.perform(get("/reservation/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Reservation not found"));

        verify(reservationService, times(1)).getReservation(999L);
    }

    @Test
    void testCreateReservation_ValidInput_Success() throws Exception {
        // Arrange
        when(reservationService.createReservation(any(ReservationForm.class), isNull()))
            .thenReturn(testReservationDTO);

        // Act & Assert
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReservationForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservationId").value(1))
            .andExpect(jsonPath("$.destination").value("Paris"));

        verify(reservationService, times(1)).createReservation(any(ReservationForm.class), isNull());
    }

    @Test
    void testCreateReservation_InvalidInput_ReturnsBadRequest() throws Exception {
        // Arrange - Create invalid form (missing required fields)
        ReservationForm invalidForm = new ReservationForm();
        // clientId, destination, participants are required but not set

        // Act & Assert
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidForm)))
            .andExpect(status().isBadRequest());

        verify(reservationService, never()).createReservation(any(), any());
    }

    @Test
    void testCreateReservation_PastDepartureDate_ReturnsBadRequest() throws Exception {
        // Arrange
        testReservationForm.setDepartureDate(LocalDate.now().minusDays(5));

        // Act & Assert
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReservationForm)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());

        verify(reservationService, never()).createReservation(any(), any());
    }

    @Test
    void testCreateReservation_EmptyParticipants_ReturnsBadRequest() throws Exception {
        // Arrange
        testReservationForm.setParticipants(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReservationForm)))
            .andExpect(status().isBadRequest());

        verify(reservationService, never()).createReservation(any(), any());
    }

    @Test
    void testCreateReservation_NegativePrice_ReturnsBadRequest() throws Exception {
        // Arrange
        testReservationForm.setTotalPrice(-100.0);

        // Act & Assert
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReservationForm)))
            .andExpect(status().isBadRequest());

        verify(reservationService, never()).createReservation(any(), any());
    }

    @Test
    void testCreateReservation_ValidationError_ReturnsBadRequest() throws Exception {
        // Arrange
        when(reservationService.createReservation(any(ReservationForm.class), isNull()))
            .thenThrow(new IllegalArgumentException("Invalid data"));

        // Act & Assert
        mockMvc.perform(post("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReservationForm)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid data"));
    }

    @Test
    void testPatchReservation_Success() throws Exception {
        // Arrange
        when(reservationService.patchReservation(eq(1L), any(ReservationPatchDTO.class)))
            .thenReturn(testReservationDTO);

        // Act & Assert
        mockMvc.perform(patch("/reservation/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReservationPatchDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservationId").value(1));

        verify(reservationService, times(1)).patchReservation(eq(1L), any(ReservationPatchDTO.class));
    }

    @Test
    void testPatchReservation_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(reservationService.patchReservation(eq(999L), any(ReservationPatchDTO.class)))
            .thenThrow(new EntityNotFoundException("Reservation not found"));

        // Act & Assert
        mockMvc.perform(patch("/reservation/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReservationPatchDTO)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Reservation not found"));

        verify(reservationService, times(1)).patchReservation(eq(999L), any(ReservationPatchDTO.class));
    }

    @Test
    void testPatchReservation_InvalidInput_ReturnsBadRequest() throws Exception {
        // Arrange
        testReservationPatchDTO.setRoomNo(100); // Exceeds max of 20

        // Act & Assert
        mockMvc.perform(patch("/reservation/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReservationPatchDTO)))
            .andExpect(status().isBadRequest());

        verify(reservationService, never()).patchReservation(anyLong(), any());
    }

    @Test
    void testPatchReservation_ValidationError_ReturnsBadRequest() throws Exception {
        // Arrange
        when(reservationService.patchReservation(eq(1L), any(ReservationPatchDTO.class)))
            .thenThrow(new IllegalArgumentException("Invalid data"));

        // Act & Assert
        mockMvc.perform(patch("/reservation/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testReservationPatchDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid data"));
    }

    @Test
    void testDeleteReservation_Success() throws Exception {
        // Arrange
        doNothing().when(reservationService).deleteReservationById(1L);

        // Act & Assert
        mockMvc.perform(delete("/reservation/1"))
            .andExpect(status().isNoContent());

        verify(reservationService, times(1)).deleteReservationById(1L);
    }

    @Test
    void testDeleteReservation_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        doThrow(new EntityNotFoundException("Reservation not found"))
            .when(reservationService).deleteReservationById(999L);

        // Act & Assert
        mockMvc.perform(delete("/reservation/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Reservation not found"));

        verify(reservationService, times(1)).deleteReservationById(999L);
    }
}
