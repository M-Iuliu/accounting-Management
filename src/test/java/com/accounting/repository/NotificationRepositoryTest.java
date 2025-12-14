package com.accounting.repository;

import com.accounting.entity.Client;
import com.accounting.entity.Notification;
import com.accounting.entity.enums.ContextType;
import com.accounting.entity.enums.NotificationCategory;
import com.accounting.entity.enums.NotificationStatus;
import com.accounting.entity.enums.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for NotificationRepository using real database operations.
 */
@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Client testClient1;
    private Client testClient2;
    private Notification notification1;
    private Notification notification2;
    private Notification notification3;
    private Notification notification4;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        notificationRepository.deleteAll();
        clientRepository.deleteAll();

        // Create test clients
        testClient1 = new Client();
        testClient1.setName("John");
        testClient1.setSurname("Doe");
        testClient1.setEmail("john.doe@example.com");
        testClient1.setTelephone("1234567890");
        testClient1.setIsDeleted(false);
        testClient1 = clientRepository.save(testClient1);

        testClient2 = new Client();
        testClient2.setName("Jane");
        testClient2.setSurname("Smith");
        testClient2.setEmail("jane.smith@example.com");
        testClient2.setTelephone("0987654321");
        testClient2.setIsDeleted(false);
        testClient2 = clientRepository.save(testClient2);

        // Create notification 1 - Payment reminder for reservation
        notification1 = new Notification();
        notification1.setClient(testClient1);
        notification1.setContextType(ContextType.RESERVATION);
        notification1.setContextId(100L);
        notification1.setNotificationStatus(NotificationStatus.UNSEEN);
        notification1.setNotificationCategory(NotificationCategory.NEW);
        notification1.setNotificationType(NotificationType.RESERVATION_PAYMENT);
        notification1.setMessage("Payment due for reservation BK001");

        // Create notification 2 - Departure reminder for same client
        notification2 = new Notification();
        notification2.setClient(testClient1);
        notification2.setContextType(ContextType.RESERVATION);
        notification2.setContextId(101L);
        notification2.setNotificationStatus(NotificationStatus.UNSEEN);
        notification2.setNotificationCategory(NotificationCategory.ACTIVE);
        notification2.setNotificationType(NotificationType.RESERVATION_DEPARTURE);
        notification2.setMessage("Departure in 3 days for reservation BK002");

        // Create notification 3 - Offer expiry for different client
        notification3 = new Notification();
        notification3.setClient(testClient2);
        notification3.setContextType(ContextType.OFFER);
        notification3.setContextId(200L);
        notification3.setNotificationStatus(NotificationStatus.UNSEEN);
        notification3.setNotificationCategory(NotificationCategory.ACTIVE);
        notification3.setNotificationType(NotificationType.OFFER);
        notification3.setMessage("Offer expires soon");

        // Create notification 4 - Dismissed notification
        notification4 = new Notification();
        notification4.setClient(testClient1);
        notification4.setContextType(ContextType.RESERVATION);
        notification4.setContextId(102L);
        notification4.setNotificationStatus(NotificationStatus.SEEN);
        notification4.setNotificationCategory(NotificationCategory.ARCHIVED);
        notification4.setNotificationType(NotificationType.RESERVATION_PAYMENT);
        notification4.setMessage("Old payment reminder");

        // Persist test data
        entityManager.persist(notification1);
        entityManager.persist(notification2);
        entityManager.persist(notification3);
        entityManager.persist(notification4);
        entityManager.flush();
    }

    @Test
    void testFindByContextTypeAndContextId_ReturnsMatchingNotifications() {
        // Act
        List<Notification> result = notificationRepository.findByContextTypeAndContextId(
            ContextType.RESERVATION, 100L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(NotificationType.RESERVATION_PAYMENT, result.get(0).getNotificationType());
        assertEquals("Payment due for reservation BK001", result.get(0).getMessage());
    }

    @Test
    void testFindByContextTypeAndContextId_MultipleResults_ReturnsAll() {
        // Arrange - Create another notification for the same context
        Notification extraNotification = new Notification();
        extraNotification.setClient(testClient1);
        extraNotification.setContextType(ContextType.RESERVATION);
        extraNotification.setContextId(100L);
        extraNotification.setNotificationStatus(NotificationStatus.UNSEEN);
        extraNotification.setNotificationCategory(NotificationCategory.ACTIVE);
        extraNotification.setNotificationType(NotificationType.RESERVATION_DEPARTURE);
        extraNotification.setMessage("Another notification for same reservation");
        notificationRepository.save(extraNotification);

        // Act
        List<Notification> result = notificationRepository.findByContextTypeAndContextId(
            ContextType.RESERVATION, 100L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testFindByContextTypeAndContextId_NoMatch_ReturnsEmpty() {
        // Act
        List<Notification> result = notificationRepository.findByContextTypeAndContextId(
            ContextType.OFFER, 999L);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testFindByNotificationCategory_ReturnsMatchingNotifications() {
        // Act
        List<Notification> result = notificationRepository.findByNotificationCategory(
            NotificationCategory.NEW);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(NotificationType.RESERVATION_PAYMENT, result.get(0).getNotificationType());
        assertEquals(testClient1.getClientId(), result.get(0).getClient().getClientId());
    }

    @Test
    void testFindByNotificationCategory_MultipleResults_ReturnsAll() {
        // Arrange - Create another urgent notification
        Notification urgentNotification = new Notification();
        urgentNotification.setClient(testClient2);
        urgentNotification.setContextType(ContextType.RESERVATION);
        urgentNotification.setContextId(300L);
        urgentNotification.setNotificationStatus(NotificationStatus.UNSEEN);
        urgentNotification.setNotificationCategory(NotificationCategory.NEW);
        urgentNotification.setNotificationType(NotificationType.RESERVATION_PAYMENT);
        urgentNotification.setMessage("Urgent payment required");
        notificationRepository.save(urgentNotification);

        // Act
        List<Notification> result = notificationRepository.findByNotificationCategory(
            NotificationCategory.NEW);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testExistsByContextTypeAndContextIdAndNotificationType_Exists_ReturnsTrue() {
        // Act
        boolean exists = notificationRepository.existsByContextTypeAndContextIdAndNotificationType(
            ContextType.RESERVATION, 100L, NotificationType.RESERVATION_PAYMENT);

        // Assert
        assertTrue(exists);
    }

    @Test
    void testExistsByContextTypeAndContextIdAndNotificationType_NotExists_ReturnsFalse() {
        // Act
        boolean exists = notificationRepository.existsByContextTypeAndContextIdAndNotificationType(
            ContextType.RESERVATION, 100L, NotificationType.OFFER);

        // Assert
        assertFalse(exists);
    }

    @Test
    void testExistsByContextTypeAndContextIdAndNotificationType_NonExistentContext_ReturnsFalse() {
        // Act
        boolean exists = notificationRepository.existsByContextTypeAndContextIdAndNotificationType(
            ContextType.OFFER, 999L, NotificationType.RESERVATION_PAYMENT);

        // Assert
        assertFalse(exists);
    }

    @Test
    void testFindNotificationToArchive_ReturnsMatchingNotifications() {
        // Arrange
        LocalDate targetDate = LocalDate.now();

        // Set modified date for notification4
        notification4.setModifiedDate(targetDate.atStartOfDay());
        notificationRepository.save(notification4);
        entityManager.flush();

        // Act
        List<Notification> result = notificationRepository.findNotificationToArchive(
            targetDate, NotificationCategory.ARCHIVED);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(notification4.getNotificationId(), result.get(0).getNotificationId());
        assertEquals(NotificationCategory.ARCHIVED, result.get(0).getNotificationCategory());
    }

    @Test
    void testFindNotificationToArchive_NoMatch_ReturnsEmpty() {
        // Arrange
        LocalDate targetDate = LocalDate.now().minusDays(100);

        // Act
        List<Notification> result = notificationRepository.findNotificationToArchive(
            targetDate, NotificationCategory.ARCHIVED);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testFindByClientNameOrPhone_WithClientName_ReturnsNotifications() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Notification> result = notificationRepository.findByClientNameOrPhone("John", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements()); // notification1, notification2, notification4
        assertTrue(result.getContent().stream()
            .allMatch(n -> n.getClient().getName().equals("John")));
    }

    @Test
    void testFindByClientNameOrPhone_WithClientSurname_ReturnsNotifications() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Notification> result = notificationRepository.findByClientNameOrPhone("Smith", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements()); // notification3
        assertEquals(testClient2.getClientId(), result.getContent().get(0).getClient().getClientId());
    }

    @Test
    void testFindByClientNameOrPhone_WithTelephone_ReturnsNotifications() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Notification> result = notificationRepository.findByClientNameOrPhone("1234567890", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements()); // All notifications for testClient1
    }

    @Test
    void testFindByClientNameOrPhone_NoMatch_ReturnsEmpty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Notification> result = notificationRepository.findByClientNameOrPhone("NonExistent", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testFindByClientNameOrPhone_CaseInsensitive_ReturnsResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Notification> result = notificationRepository.findByClientNameOrPhone("john", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
    }

    @Test
    void testFindById_ReturnsNotification() {
        // Act
        Notification found = notificationRepository.findById(notification1.getNotificationId()).orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals(notification1.getNotificationId(), found.getNotificationId());
        assertEquals("Payment due for reservation BK001", found.getMessage());
        assertEquals(ContextType.RESERVATION, found.getContextType());
    }

    @Test
    void testSaveNotification_Success() {
        // Arrange
        Notification newNotification = new Notification();
        newNotification.setClient(testClient1);
        newNotification.setContextType(ContextType.OFFER);
        newNotification.setContextId(500L);
        newNotification.setNotificationStatus(NotificationStatus.UNSEEN);
        newNotification.setNotificationCategory(NotificationCategory.ACTIVE);
        newNotification.setNotificationType(NotificationType.OFFER);
        newNotification.setMessage("New offer expiry notification");

        // Act
        Notification saved = notificationRepository.save(newNotification);

        // Assert
        assertNotNull(saved.getNotificationId());
        assertEquals("New offer expiry notification", saved.getMessage());

        // Verify it can be found
        Notification found = notificationRepository.findById(saved.getNotificationId()).orElse(null);
        assertNotNull(found);
        assertEquals("New offer expiry notification", found.getMessage());
    }

    @Test
    void testUpdateNotification_Success() {
        // Arrange
        Notification notification = notificationRepository.findById(notification1.getNotificationId()).orElseThrow();
        String originalMessage = notification.getMessage();

        // Act
        notification.setMessage("Updated payment reminder");
        notification.setNotificationStatus(NotificationStatus.SEEN);
        notificationRepository.save(notification);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Notification updated = notificationRepository.findById(notification1.getNotificationId()).orElseThrow();
        assertEquals("Updated payment reminder", updated.getMessage());
        assertEquals(NotificationStatus.SEEN, updated.getNotificationStatus());
        assertNotEquals(originalMessage, updated.getMessage());
    }

    @Test
    void testDeleteNotification_Success() {
        // Arrange
        Long notificationId = notification1.getNotificationId();

        // Verify notification exists
        assertTrue(notificationRepository.findById(notificationId).isPresent());

        // Act
        notificationRepository.deleteById(notificationId);
        entityManager.flush();

        // Assert
        assertFalse(notificationRepository.findById(notificationId).isPresent());
    }
}
