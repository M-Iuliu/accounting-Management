package com.accounting.service.clients;

import com.accounting.dto.client.ClientDTO;
import com.accounting.dto.client.ClientShortDTO;
import com.accounting.entity.Client;
import com.accounting.mapper.ClientMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceHelperTest {

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceHelper clientServiceHelper;

    private Client testClient;
    private ClientDTO testClientDTO;
    private ClientShortDTO testClientShortDTO;

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

        testClientShortDTO = new ClientShortDTO();
        testClientShortDTO.setClientId(1L);
        testClientShortDTO.setFullName("John Doe");
        testClientShortDTO.setTelephone("1234567890");
    }

    @Test
    void testMapClientToDTO_Success() {
        // Arrange
        when(clientMapper.toDTO(testClient)).thenReturn(testClientDTO);

        // Act
        ClientDTO result = clientServiceHelper.mapClientToDTO(testClient);

        // Assert
        assertNotNull(result);
        assertEquals(testClientDTO.getClientId(), result.getClientId());
        assertEquals(testClientDTO.getName(), result.getName());
        assertEquals(testClientDTO.getSurname(), result.getSurname());
        verify(clientMapper, times(1)).toDTO(testClient);
    }

    @Test
    void testMapClientToShortDTO_Success() {
        // Arrange
        when(clientMapper.toShortDTO(testClient)).thenReturn(testClientShortDTO);

        // Act
        ClientShortDTO result = clientServiceHelper.mapClientToShortDTO(testClient);

        // Assert
        assertNotNull(result);
        assertEquals(testClientShortDTO.getClientId(), result.getClientId());
        assertEquals(testClientShortDTO.getFullName(), result.getFullName());
        assertEquals(testClientShortDTO.getTelephone(), result.getTelephone());
        verify(clientMapper, times(1)).toShortDTO(testClient);
    }

    @Test
    void testValidateClient_Success() {
        // Act & Assert
        assertDoesNotThrow(() -> clientServiceHelper.validateClient(testClient));
    }

    @Test
    void testValidateClient_FullNameTooLong_ThrowsException() {
        // Arrange - Create a client with very long name
        Client longNameClient = new Client();
        longNameClient.setName("A".repeat(150));
        longNameClient.setSurname("B".repeat(100));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> clientServiceHelper.validateClient(longNameClient)
        );
        assertEquals("Full name exceeds maximum length", exception.getMessage());
    }

    @Test
    void testValidateClient_NullName_Success() {
        // Arrange
        testClient.setName(null);

        // Act & Assert - Should not throw exception when name is null
        assertDoesNotThrow(() -> clientServiceHelper.validateClient(testClient));
    }

    @Test
    void testValidateClient_NullSurname_Success() {
        // Arrange
        testClient.setSurname(null);

        // Act & Assert - Should not throw exception when surname is null
        assertDoesNotThrow(() -> clientServiceHelper.validateClient(testClient));
    }
}
