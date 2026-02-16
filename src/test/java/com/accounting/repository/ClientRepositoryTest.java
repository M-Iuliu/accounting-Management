package com.accounting.repository;

import com.accounting.entity.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ClientRepository using real database operations.
 * Tests the critical bug fix: deleted clients should not appear in search results.
 */
@DataJpaTest
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Client activeClient1;
    private Client activeClient2;
    private Client deletedClient;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        clientRepository.deleteAll();

        // Create active clients
        activeClient1 = new Client();
        activeClient1.setName("John");
        activeClient1.setSurname("Doe");
        activeClient1.setEmail("john.doe@example.com");
        activeClient1.setTelephone("1234567890");
        activeClient1.setIsDeleted(false);

        activeClient2 = new Client();
        activeClient2.setName("Jane");
        activeClient2.setSurname("Smith");
        activeClient2.setEmail("jane.smith@example.com");
        activeClient2.setTelephone("0987654321");
        activeClient2.setIsDeleted(false);

        // Create deleted client
        deletedClient = new Client();
        deletedClient.setName("John");
        deletedClient.setSurname("Deleted");
        deletedClient.setEmail("deleted@example.com");
        deletedClient.setTelephone("5555555555");
        deletedClient.setIsDeleted(true);
        deletedClient.setDeletionDate(new Date());

        // Persist test data
        entityManager.persist(activeClient1);
        entityManager.persist(activeClient2);
        entityManager.persist(deletedClient);
        entityManager.flush();
    }

    @Test
    void testFindAllActiveClients_ReturnsOnlyActiveClients() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Client> result = clientRepository.findAllActiveClients(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().noneMatch(Client::getIsDeleted));
    }

    @Test
    void testFindByFilter_WithNameMatch_ReturnsActiveOnly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act - Search for "John" (matches both activeClient1 and deletedClient)
        Page<Client> result = clientRepository.findByFilter("John", pageable);

        // Assert - Should only return activeClient1, NOT deletedClient
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).getName());
        assertEquals("Doe", result.getContent().get(0).getSurname());
        assertFalse(result.getContent().get(0).getIsDeleted());
    }

    @Test
    void testFindByFilter_WithSurnameMatch_ReturnsActiveOnly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Client> result = clientRepository.findByFilter("Smith", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Jane", result.getContent().get(0).getName());
    }

    @Test
    void testFindByFilter_WithTelephoneMatch_ReturnsActiveOnly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Client> result = clientRepository.findByFilter("1234567890", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).getName());
        assertEquals("Doe", result.getContent().get(0).getSurname());
    }

    @Test
    void testFindByFilter_WithDeletedClientTelephone_ReturnsEmpty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act - Search for deleted client's telephone
        Page<Client> result = clientRepository.findByFilter("5555555555", pageable);

        // Assert - Should return empty because client is deleted
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testFindByFilter_NoMatch_ReturnsEmpty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Client> result = clientRepository.findByFilter("NonExistent", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testFindByFilter_CaseInsensitive_ReturnsResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Client> result = clientRepository.findByFilter("john", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testFindById_ReturnsClient() {
        // Act
        Client found = clientRepository.findById(activeClient1.getClientId()).orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals(activeClient1.getClientId(), found.getClientId());
        assertEquals("John", found.getName());
    }

    @Test
    void testSaveClient_Success() {
        // Arrange
        Client newClient = new Client();
        newClient.setName("Test");
        newClient.setSurname("User");
        newClient.setEmail("test@example.com");
        newClient.setTelephone("1111111111");
        newClient.setIsDeleted(false);

        // Act
        Client saved = clientRepository.save(newClient);

        // Assert
        assertNotNull(saved.getClientId());
        assertEquals("Test", saved.getName());

        // Verify it can be found
        Client found = clientRepository.findById(saved.getClientId()).orElse(null);
        assertNotNull(found);
        assertEquals("Test", found.getName());
    }

    @Test
    void testUpdateClient_Success() {
        // Arrange
        Client client = clientRepository.findById(activeClient1.getClientId()).orElseThrow();
        String originalName = client.getName();

        // Act
        client.setName("UpdatedName");
        clientRepository.save(client);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Client updated = clientRepository.findById(activeClient1.getClientId()).orElseThrow();
        assertEquals("UpdatedName", updated.getName());
        assertNotEquals(originalName, updated.getName());
    }

    @Test
    void testSoftDelete_ClientNotReturnedInActiveSearch() {
        // Arrange
        Client client = clientRepository.findById(activeClient1.getClientId()).orElseThrow();
        Pageable pageable = PageRequest.of(0, 10);

        // Verify client is returned before deletion
        Page<Client> beforeDelete = clientRepository.findAllActiveClients(pageable);
        assertEquals(2, beforeDelete.getTotalElements());

        // Act - Soft delete
        client.setIsDeleted(true);
        client.setDeletionDate(new Date());
        clientRepository.save(client);
        entityManager.flush();
        entityManager.clear();

        // Assert - Client should not appear in active clients
        Page<Client> afterDelete = clientRepository.findAllActiveClients(pageable);
        assertEquals(1, afterDelete.getTotalElements());
        assertFalse(afterDelete.getContent().stream()
            .anyMatch(c -> c.getClientId().equals(activeClient1.getClientId())));
    }
}
