package com.accounting.service.notification;

import com.accounting.dto.notification.NotificationDTO;
import com.accounting.entity.Notification;
import com.accounting.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Helper component for Notification service operations.
 * Provides utility methods and delegates to NotificationMapper for entity-DTO conversions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationServiceHelper {

    private final NotificationMapper notificationMapper;

    /**
     * Maps Notification entity to NotificationDTO
     * @param notification the notification entity
     * @return NotificationDTO representation
     */
    public NotificationDTO mapToDto(Notification notification) {
        log.debug("Mapping Notification entity to DTO for notification ID: {}", notification.getNotificationId());
        return notificationMapper.toDTO(notification);
    }

    /**
     * Validates notification business rules before save/update
     * @param notification the notification to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateNotification(Notification notification) {
        log.debug("Validating notification: {}", notification.getNotificationId());

        if (notification.getClient() == null) {
            throw new IllegalArgumentException("Notification must have a client");
        }

        if (notification.getContextType() == null) {
            throw new IllegalArgumentException("Notification must have a context type");
        }

        if (notification.getContextId() == null) {
            throw new IllegalArgumentException("Notification must have a context ID");
        }

        if (notification.getNotificationType() == null) {
            throw new IllegalArgumentException("Notification must have a type");
        }

        if (notification.getMessage() == null || notification.getMessage().isBlank()) {
            throw new IllegalArgumentException("Notification must have a message");
        }

        log.debug("Notification validation successful");
    }
}
