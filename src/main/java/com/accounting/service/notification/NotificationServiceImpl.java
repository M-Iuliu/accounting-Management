package com.accounting.service.notification;

import com.accounting.dto.notification.NotificationDTO;
import com.accounting.dto.notification.NotificationDataDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.pagination.PaginationDTO;
import com.accounting.entity.Client;
import com.accounting.entity.Notification;
import com.accounting.entity.enums.ContextType;
import com.accounting.entity.enums.NotificationCategory;
import com.accounting.entity.enums.NotificationStatus;
import com.accounting.entity.enums.NotificationType;
import com.accounting.repository.NotificationRepository;
import com.accounting.service.clients.ClientService;
import com.accounting.service.comment.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for Notification domain operations.
 * Handles business logic, transaction management, and orchestrates
 * interactions between controller, repository, and mapper layers.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ClientService clientService;
    private final CommentService commentService;
    private final NotificationServiceHelper notificationServiceHelper;

    /**
     * Creates a new notification for a client
     * @param client the client for this notification
     * @param message the notification message
     * @param contextType the context type (OFFER or RESERVATION)
     * @param contextId the context ID
     * @param type the notification type
     * @return the created notification entity
     */
    @Override
    @Transactional
    public Notification createNotification(Client client, String message, ContextType contextType, Long contextId, NotificationType type) {
        log.info("Creating notification for client ID: {}, type: {}, context: {} ID: {}",
            client.getClientId(), type, contextType, contextId);

        Notification notification = new Notification();
        notification.setClient(client);
        notification.setMessage(message);
        notification.setContextType(contextType);
        notification.setContextId(contextId);
        notification.setNotificationCategory(NotificationCategory.NEW);
        notification.setNotificationStatus(NotificationStatus.UNSEEN);
        notification.setNotificationType(type);

        notificationServiceHelper.validateNotification(notification);
        notification = notificationRepository.save(notification);

        log.info("Successfully created notification with ID: {}", notification.getNotificationId());
        return notification;
    }

    /**
     * Dismisses a notification by updating its category/status
     * @param notificationId the notification ID to dismiss
     * @param message optional comment message
     */
    @Override
    @Transactional
    public void dismissNotification(Long notificationId, String message) {
        log.info("Dismissing notification with ID: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    log.warn("Notification not found with ID: {}", notificationId);
                    return new EntityNotFoundException("No notification found for notificationId: " + notificationId);
                });

        if (NotificationCategory.NEW.equals(notification.getNotificationCategory())) {
            log.debug("Moving notification {} from NEW to ACTIVE", notificationId);
            notification.setNotificationCategory(NotificationCategory.ACTIVE);
            notification.setNotificationStatus(NotificationStatus.SEEN);
        } else if (NotificationCategory.ACTIVE.equals(notification.getNotificationCategory())) {
            log.debug("Moving notification {} from ACTIVE to ARCHIVED", notificationId);
            notification.setNotificationCategory(NotificationCategory.ARCHIVED);
        }

        if (message != null && !message.isBlank()) {
            log.debug("Adding comment to notification {}", notificationId);
            commentService.createCommentOnDismissNotification(notification, message);
        }

        notificationRepository.save(notification);
        log.info("Successfully dismissed notification with ID: {}", notificationId);
    }

    /**
     * Gets notifications by context type and ID
     * @param contextType the context type (OFFER or RESERVATION)
     * @param contextId the context ID
     * @return list of notification DTOs
     */
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByContext(String contextType, Long contextId) {
        log.info("Getting notifications for context: {} ID: {}", contextType, contextId);

        ContextType context = ContextType.valueOf(contextType.toUpperCase());
        List<Notification> notifications = notificationRepository.findByContextTypeAndContextId(context, contextId);

        log.info("Found {} notifications for context {} ID {}", notifications.size(), contextType, contextId);
        return mapToListDto(notifications);
    }

    /**
     * Gets paginated notifications filtered by client name or phone
     * @param input search filter (client name, surname, or phone)
     * @param page page number (0-indexed)
     * @param size page size
     * @return paginated notification list with metadata
     */
    @Override
    @Transactional(readOnly = true)
    public PageDTO getNotificationsByClient(String input, int page, int size) {
        log.info("Getting notifications by client - page: {}, size: {}, filter: '{}'", page, size, input);

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationPage = notificationRepository.findByClientNameOrPhone(input.trim(), pageable);

        List<NotificationDTO> dtoList = notificationPage.getContent()
                .stream()
                .map(this::mapToDtoWithRelations)
                .toList();

        PaginationDTO pagination = new PaginationDTO(
                notificationPage.getTotalElements(),
                notificationPage.getSize(),
                List.of(5, 10, 20),
                notificationPage.getNumber()
        );

        log.info("Retrieved {} notifications out of {} total", dtoList.size(), notificationPage.getTotalElements());
        return new PageDTO<>(dtoList, pagination);
    }

    /**
     * Gets all notifications grouped by category
     * @return NotificationDataDTO with urgent, active, and archive lists
     */
    @Override
    @Transactional(readOnly = true)
    public NotificationDataDTO getNotificationsCategories() {
        log.info("Getting notifications categorized by status");

        NotificationDataDTO notificationDataDTO = new NotificationDataDTO();
        notificationDataDTO.setArchiveList(getNotificationsByType(NotificationCategory.ARCHIVED));
        notificationDataDTO.setActiveList(getNotificationsByType(NotificationCategory.ACTIVE));
        notificationDataDTO.setUrgentList(getNotificationsByType(NotificationCategory.NEW));

        int totalCount = notificationDataDTO.getArchiveList().size() +
                        notificationDataDTO.getActiveList().size() +
                        notificationDataDTO.getUrgentList().size();
        log.info("Retrieved {} notifications across all categories", totalCount);

        return notificationDataDTO;
    }

    /**
     * Gets notifications by category
     * @param status the notification category
     * @return list of notification DTOs
     */
    private List<NotificationDTO> getNotificationsByType(NotificationCategory status) {
        log.debug("Getting notifications with category: {}", status);
        return mapToListDto(notificationRepository.findByNotificationCategory(status));
    }

    /**
     * Maps list of notifications to DTOs with client and comments
     * @param notifications list of notification entities
     * @return list of notification DTOs
     */
    private List<NotificationDTO> mapToListDto(List<Notification> notifications) {
        return notifications.stream()
                .map(this::mapToDtoWithRelations)
                .toList();
    }

    /**
     * Maps notification to DTO and populates client and comments
     * @param notification the notification entity
     * @return NotificationDTO with client and comments
     */
    private NotificationDTO mapToDtoWithRelations(Notification notification) {
        NotificationDTO dto = notificationServiceHelper.mapToDto(notification);
        dto.setClient(clientService.mapToShortDto(notification.getClient()));
        dto.setCommentList(commentService.getComments(
                notification.getContextType().toString(),
                notification.getContextId()));
        return dto;
    }
}
