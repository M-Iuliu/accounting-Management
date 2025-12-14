package com.accounting.service.clients;

import com.accounting.dto.client.ClientAddEditForm;
import com.accounting.dto.client.ClientDTO;
import com.accounting.dto.client.ClientShortDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.entity.Client;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.mapper.ClientMapper;
import com.accounting.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private ClientServiceHelper clientServiceHelper;

    @InjectMocks
    private ClientServiceImp clientService;

    private Client testClient;
    private ClientDTO testClientDTO;
    private ClientShortDTO testClientShortDTO;
    private ClientAddEditForm testClientForm;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setClientId(1L);
        testClient.setName("John");
        testClient.setSurname("Doe");
        testClient.setEmail("john.doe@example.com");
        testClient.setTelephone("1234567890");
        testClient.setIsDeleted(false);

        testClientDTO = new ClientDTO();
        testClientDTO.setClientId(1L);
        testClientDTO.setName("John");
        testClientDTO.setSurname("Doe");
        testClientDTO.setEmail("john.doe@example.com");

        testClientShortDTO = new ClientShortDTO();
        testClientShortDTO.setClientId(1L);
        testClientShortDTO.setFullName("John Doe");
        testClientShortDTO.setTelephone("1234567890");

        testClientForm = new ClientAddEditForm();
        testClientForm.setName("John");
        testClientForm.setSurname("Doe");
        testClientForm.setEmail("john.doe@example.com");
        testClientForm.setTelephone("1234567890");
    }

    @Test
    void testFindById_Success() throws ClientNotFoundException {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        // Act
        Client result = clientService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testClient.getClientId(), result.getClientId());
        assertEquals(testClient.getName(), result.getName());
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound_ThrowsException() {
        // Arrange
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ClientNotFoundException exception = assertThrows(
            ClientNotFoundException.class,
            () -> clientService.findById(999L)
        );
        assertEquals("Client not found with ID: 999", exception.getMessage());
        verify(clientRepository, times(1)).findById(999L);
    }

    @Test
    void testFindById_DeletedClient_ThrowsException() {
        // Arrange
        testClient.setIsDeleted(true);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        // Act & Assert
        ClientNotFoundException exception = assertThrows(
            ClientNotFoundException.class,
            () -> clientService.findById(1L)
        );
        assertEquals("Client with ID 1 is inactive", exception.getMessage());
        verify(clientRepository, times(1)).findById(1L);
    }

    @Test
    void testGetClients_WithoutFilter_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Client> clientPage = new PageImpl<>(List.of(testClient), pageable, 1);

        when(clientRepository.findAllActiveClients(any(Pageable.class))).thenReturn(clientPage);
        when(clientServiceHelper.mapClientToDTO(testClient)).thenReturn(testClientDTO);

        // Act
        PageDTO result = clientService.getClients(null, 0, 10);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, ((List<?>) result.getItems()).size());
        verify(clientRepository, times(1)).findAllActiveClients(any(Pageable.class));
        verify(clientRepository, never()).findByFilter(anyString(), any(Pageable.class));
    }

    @Test
    void testGetClients_WithFilter_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Client> clientPage = new PageImpl<>(List.of(testClient), pageable, 1);

        when(clientRepository.findByFilter(eq("John"), any(Pageable.class))).thenReturn(clientPage);
        when(clientServiceHelper.mapClientToDTO(testClient)).thenReturn(testClientDTO);

        // Act
        PageDTO result = clientService.getClients("John", 0, 10);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, ((List<?>) result.getItems()).size());
        verify(clientRepository, times(1)).findByFilter(eq("John"), any(Pageable.class));
        verify(clientRepository, never()).findAllActiveClients(any(Pageable.class));
    }

    @Test
    void testGetClients_EmptyFilter_UsesAllActiveClients() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Client> clientPage = new PageImpl<>(List.of(), pageable, 0);

        when(clientRepository.findAllActiveClients(any(Pageable.class))).thenReturn(clientPage);

        // Act
        PageDTO result = clientService.getClients("   ", 0, 10);

        // Assert
        assertNotNull(result);
        verify(clientRepository, times(1)).findAllActiveClients(any(Pageable.class));
        verify(clientRepository, never()).findByFilter(anyString(), any(Pageable.class));
    }

    @Test
    void testSaveClient_Success() {
        // Arrange
        when(clientMapper.toEntity(testClientForm)).thenReturn(testClient);
        when(clientRepository.save(testClient)).thenReturn(testClient);
        doNothing().when(clientServiceHelper).validateClient(testClient);

        // Act
        Client result = clientService.saveClient(testClientForm);

        // Assert
        assertNotNull(result);
        assertEquals(testClient.getClientId(), result.getClientId());
        verify(clientMapper, times(1)).toEntity(testClientForm);
        verify(clientServiceHelper, times(1)).validateClient(testClient);
        verify(clientRepository, times(1)).save(testClient);
    }

    @Test
    void testSaveClient_ValidationFails_ThrowsException() {
        // Arrange
        when(clientMapper.toEntity(testClientForm)).thenReturn(testClient);
        doThrow(new IllegalArgumentException("Validation failed"))
            .when(clientServiceHelper).validateClient(testClient);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> clientService.saveClient(testClientForm)
        );
        assertEquals("Validation failed", exception.getMessage());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void testEditClient_Success() throws ClientNotFoundException {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        doNothing().when(clientMapper).updateEntityFromForm(testClientForm, testClient);
        doNothing().when(clientServiceHelper).validateClient(testClient);
        when(clientRepository.save(testClient)).thenReturn(testClient);
        when(clientServiceHelper.mapClientToDTO(testClient)).thenReturn(testClientDTO);

        // Act
        ClientDTO result = clientService.editClient(1L, testClientForm);

        // Assert
        assertNotNull(result);
        assertEquals(testClientDTO.getClientId(), result.getClientId());
        verify(clientRepository, times(1)).findById(1L);
        verify(clientMapper, times(1)).updateEntityFromForm(testClientForm, testClient);
        verify(clientServiceHelper, times(1)).validateClient(testClient);
        verify(clientRepository, times(1)).save(testClient);
    }

    @Test
    void testEditClient_NotFound_ThrowsException() {
        // Arrange
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            ClientNotFoundException.class,
            () -> clientService.editClient(999L, testClientForm)
        );
        verify(clientRepository, times(1)).findById(999L);
        verify(clientRepository, never()).save(any());
    }

    @Test
    void testDeleteClientById_Success() throws ClientNotFoundException {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(testClient)).thenReturn(testClient);

        // Act
        clientService.deleteClientById(1L);

        // Assert
        assertTrue(testClient.getIsDeleted());
        assertNotNull(testClient.getDeletionDate());
        verify(clientRepository, times(1)).findById(1L);
        verify(clientRepository, times(1)).save(testClient);
    }

    @Test
    void testDeleteClientById_NotFound_ThrowsException() {
        // Arrange
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            ClientNotFoundException.class,
            () -> clientService.deleteClientById(999L)
        );
        verify(clientRepository, times(1)).findById(999L);
        verify(clientRepository, never()).save(any());
    }

    @Test
    void testMapToDto_Success() {
        // Arrange
        when(clientServiceHelper.mapClientToDTO(testClient)).thenReturn(testClientDTO);

        // Act
        ClientDTO result = clientService.mapToDto(testClient);

        // Assert
        assertNotNull(result);
        assertEquals(testClientDTO.getClientId(), result.getClientId());
        verify(clientServiceHelper, times(1)).mapClientToDTO(testClient);
    }

    @Test
    void testMapToShortDto_Success() {
        // Arrange
        when(clientServiceHelper.mapClientToShortDTO(testClient)).thenReturn(testClientShortDTO);

        // Act
        ClientShortDTO result = clientService.mapToShortDto(testClient);

        // Assert
        assertNotNull(result);
        assertEquals(testClientShortDTO.getClientId(), result.getClientId());
        verify(clientServiceHelper, times(1)).mapClientToShortDTO(testClient);
    }
}
