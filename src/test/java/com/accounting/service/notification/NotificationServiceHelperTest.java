package com.accounting.service.notification;

import com.accounting.dto.notification.NotificationDTO;
import com.accounting.entity.Client;
import com.accounting.entity.Notification;
import com.accounting.entity.enums.ContextType;
import com.accounting.entity.enums.NotificationCategory;
import com.accounting.entity.enums.NotificationStatus;
import com.accounting.entity.enums.NotificationType;
import com.accounting.mapper.NotificationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceHelperTest {

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceHelper notificationServiceHelper;

    private Notification testNotification;
    private NotificationDTO testNotificationDTO;
    private Client testClient;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setClientId(1L);
        testClient.setName("John");
        testClient.setSurname("Doe");

        testNotification = new Notification();
        testNotification.setNotificationId(1L);
        testNotification.setClient(testClient);
        testNotification.setMessage("Test notification");
        testNotification.setContextType(ContextType.OFFER);
        testNotification.setContextId(100L);
        testNotification.setNotificationCategory(NotificationCategory.NEW);
        testNotification.setNotificationStatus(NotificationStatus.UNSEEN);
        testNotification.setNotificationType(NotificationType.OFFER);

        testNotificationDTO = new NotificationDTO();
        testNotificationDTO.setNotificationId(1L);
        testNotificationDTO.setMessage("Test notification");
    }

    @Test
    void testMapToDto_Success() {
        // Arrange
        when(notificationMapper.toDTO(testNotification)).thenReturn(testNotificationDTO);

        // Act
        NotificationDTO result = notificationServiceHelper.mapToDto(testNotification);

        // Assert
        assertNotNull(result);
        assertEquals(testNotificationDTO.getNotificationId(), result.getNotificationId());
        assertEquals(testNotificationDTO.getMessage(), result.getMessage());
        verify(notificationMapper, times(1)).toDTO(testNotification);
    }

    @Test
    void testValidateNotification_Success() {
        // Act & Assert
        assertDoesNotThrow(() -> notificationServiceHelper.validateNotification(testNotification));
    }

    @Test
    void testValidateNotification_NullClient_ThrowsException() {
        // Arrange
        testNotification.setClient(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> notificationServiceHelper.validateNotification(testNotification)
        );
        assertEquals("Notification must have a client", exception.getMessage());
    }

    @Test
    void testValidateNotification_NullContextType_ThrowsException() {
        // Arrange
        testNotification.setContextType(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> notificationServiceHelper.validateNotification(testNotification)
        );
        assertEquals("Notification must have a context type", exception.getMessage());
    }

    @Test
    void testValidateNotification_NullContextId_ThrowsException() {
        // Arrange
        testNotification.setContextId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> notificationServiceHelper.validateNotification(testNotification)
        );
        assertEquals("Notification must have a context ID", exception.getMessage());
    }

    @Test
    void testValidateNotification_NullNotificationType_ThrowsException() {
        // Arrange
        testNotification.setNotificationType(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> notificationServiceHelper.validateNotification(testNotification)
        );
        assertEquals("Notification must have a type", exception.getMessage());
    }

    @Test
    void testValidateNotification_NullMessage_ThrowsException() {
        // Arrange
        testNotification.setMessage(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> notificationServiceHelper.validateNotification(testNotification)
        );
        assertEquals("Notification must have a message", exception.getMessage());
    }

    @Test
    void testValidateNotification_BlankMessage_ThrowsException() {
        // Arrange
        testNotification.setMessage("   ");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> notificationServiceHelper.validateNotification(testNotification)
        );
        assertEquals("Notification must have a message", exception.getMessage());
    }

    @Test
    void testValidateNotification_EmptyMessage_ThrowsException() {
        // Arrange
        testNotification.setMessage("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> notificationServiceHelper.validateNotification(testNotification)
        );
        assertEquals("Notification must have a message", exception.getMessage());
    }
}
