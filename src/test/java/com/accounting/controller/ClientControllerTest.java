package com.accounting.controller;

import com.accounting.dto.client.ClientAddEditForm;
import com.accounting.dto.client.ClientDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.pagination.PaginationDTO;
import com.accounting.entity.Client;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.service.clients.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    private Client testClient;
    private ClientDTO testClientDTO;
    private ClientAddEditForm testClientForm;
    private PageDTO pageDTO;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setClientId(1L);
        testClient.setName("John");
        testClient.setSurname("Doe");
        testClient.setEmail("john.doe@example.com");
        testClient.setTelephone("1234567890");

        testClientDTO = new ClientDTO();
        testClientDTO.setClientId(1L);
        testClientDTO.setName("John");
        testClientDTO.setSurname("Doe");
        testClientDTO.setEmail("john.doe@example.com");

        testClientForm = new ClientAddEditForm();
        testClientForm.setName("John");
        testClientForm.setSurname("Doe");
        testClientForm.setEmail("john.doe@example.com");
        testClientForm.setTelephone("1234567890");

        PaginationDTO pagination = new PaginationDTO(1L, 5, List.of(5, 10, 20), 0);
        pageDTO = new PageDTO<>(List.of(testClientDTO), pagination);
    }

    @Test
    void testGetClients_WithoutFilter_Success() throws Exception {
        // Arrange
        when(clientService.getClients(isNull(), eq(0), eq(5))).thenReturn(pageDTO);

        // Act & Assert
        mockMvc.perform(get("/client")
                .param("page", "0")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(1)))
            .andExpect(jsonPath("$.data[0].name").value("John"))
            .andExpect(jsonPath("$.pagination.totalElements").value(1));

        verify(clientService, times(1)).getClients(isNull(), eq(0), eq(5));
    }

    @Test
    void testGetClients_WithFilter_Success() throws Exception {
        // Arrange
        when(clientService.getClients(eq("John"), eq(0), eq(10))).thenReturn(pageDTO);

        // Act & Assert
        mockMvc.perform(get("/client")
                .param("input", "John")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data", hasSize(1)));

        verify(clientService, times(1)).getClients(eq("John"), eq(0), eq(10));
    }

    @Test
    void testGetClients_DefaultPagination_Success() throws Exception {
        // Arrange
        when(clientService.getClients(isNull(), eq(0), eq(5))).thenReturn(pageDTO);

        // Act & Assert - Should use default page=0, size=5
        mockMvc.perform(get("/client"))
            .andExpect(status().isOk());

        verify(clientService, times(1)).getClients(isNull(), eq(0), eq(5));
    }

    @Test
    void testSaveClient_ValidInput_Success() throws Exception {
        // Arrange
        when(clientService.saveClient(any(ClientAddEditForm.class))).thenReturn(testClient);

        // Act & Assert
        mockMvc.perform(post("/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testClientForm)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clientId").value(1))
            .andExpect(jsonPath("$.name").value("John"))
            .andExpect(jsonPath("$.surname").value("Doe"));

        verify(clientService, times(1)).saveClient(any(ClientAddEditForm.class));
    }

    @Test
    void testSaveClient_InvalidInput_ReturnsBadRequest() throws Exception {
        // Arrange - Create invalid form (missing required fields)
        ClientAddEditForm invalidForm = new ClientAddEditForm();
        // name and surname are required but not set

        // Act & Assert
        mockMvc.perform(post("/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidForm)))
            .andExpect(status().isBadRequest());

        verify(clientService, never()).saveClient(any());
    }

    @Test
    void testSaveClient_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        testClientForm.setEmail("invalid-email");

        // Act & Assert
        mockMvc.perform(post("/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testClientForm)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());

        verify(clientService, never()).saveClient(any());
    }

    @Test
    void testEditClient_Success() throws Exception {
        // Arrange
        when(clientService.editClient(eq(1L), any(ClientAddEditForm.class))).thenReturn(testClientDTO);

        // Act & Assert
        mockMvc.perform(put("/client/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testClientForm)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clientId").value(1))
            .andExpect(jsonPath("$.name").value("John"));

        verify(clientService, times(1)).editClient(eq(1L), any(ClientAddEditForm.class));
    }

    @Test
    void testEditClient_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(clientService.editClient(eq(999L), any(ClientAddEditForm.class)))
            .thenThrow(new ClientNotFoundException("Client not found with ID: 999"));

        // Act & Assert
        mockMvc.perform(put("/client/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testClientForm)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Client not found with ID: 999"));

        verify(clientService, times(1)).editClient(eq(999L), any(ClientAddEditForm.class));
    }

    @Test
    void testEditClient_InvalidInput_ReturnsBadRequest() throws Exception {
        // Arrange
        ClientAddEditForm invalidForm = new ClientAddEditForm();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(put("/client/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidForm)))
            .andExpect(status().isBadRequest());

        verify(clientService, never()).editClient(anyLong(), any());
    }

    @Test
    void testEditClient_ValidationError_ReturnsBadRequest() throws Exception {
        // Arrange
        when(clientService.editClient(eq(1L), any(ClientAddEditForm.class)))
            .thenThrow(new IllegalArgumentException("Full name exceeds maximum length"));

        // Act & Assert
        mockMvc.perform(put("/client/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testClientForm)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Full name exceeds maximum length"));
    }

    @Test
    void testDeleteClient_Success() throws Exception {
        // Arrange
        doNothing().when(clientService).deleteClientById(1L);

        // Act & Assert
        mockMvc.perform(delete("/client/1"))
            .andExpect(status().isNoContent());

        verify(clientService, times(1)).deleteClientById(1L);
    }

    @Test
    void testDeleteClient_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        doThrow(new ClientNotFoundException("Client not found with ID: 999"))
            .when(clientService).deleteClientById(999L);

        // Act & Assert
        mockMvc.perform(delete("/client/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Client not found"));

        verify(clientService, times(1)).deleteClientById(999L);
    }
}
