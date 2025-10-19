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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ClientService clientService;
    private final CommentService commentService;

    public Notification createNotification(Client client, String message, ContextType contextType, Long contextId, NotificationType type) {
        Notification notification = new Notification();
        notification.setClient(client);
        notification.setMessage(message);
        notification.setCreateDate(LocalDate.now());
        notification.setContextType(contextType);
        notification.setContextId(contextId);
        notification.setNotificationCategory(NotificationCategory.NEW);
        notification.setNotificationStatus(NotificationStatus.UNSEEN);
        notification.setNotificationType(type);

        return notificationRepository.save(notification);
    }

    public void dismissNotification(Long notificationId, String message) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() ->
                new EntityNotFoundException("No notification found for notificationId: " + notificationId));

        if (NotificationCategory.NEW.equals(notification.getNotificationCategory())) {
            notification.setNotificationCategory(NotificationCategory.ACTIVE);
            notification.setNotificationStatus(NotificationStatus.SEEN);
        } else if (NotificationCategory.ACTIVE.equals(notification.getNotificationCategory())) {
            notification.setNotificationCategory(NotificationCategory.ARCHIVED);
        }

        if (message != null)
            commentService.createCommentOnDismissNotification(notification, message);


        notificationRepository.save(notification);
    }

    public List<NotificationDTO> getNotificationsByContext(String contextType, Long contextId) {
        return mapToListDto(
                notificationRepository.findByContextTypeAndContextId(
                        ContextType.valueOf(contextType.toUpperCase()),
                        contextId));
    }

    public PageDTO getNotificationsByClient(String input, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationPage;

//        if (input != null && !input.isBlank())
        notificationPage = notificationRepository.findByClientNameOrPhone(input.trim(), pageable);

        List<NotificationDTO> dtoList = notificationPage.getContent()
                .stream()
                .map(this::mapToDto)
                .toList();

        PaginationDTO pagination = new PaginationDTO(
                notificationPage.getTotalElements(),
                notificationPage.getSize(),
                List.of(5, 10, 20),
                notificationPage.getNumber()
        );

        return new PageDTO<>(dtoList, pagination);
    }

    public NotificationDataDTO getNotificationsCategories() {
        NotificationDataDTO notificationDataDTO = new NotificationDataDTO();

        notificationDataDTO.setArchiveList(getNotificationsByType(NotificationCategory.ARCHIVED));
        notificationDataDTO.setActiveList(getNotificationsByType(NotificationCategory.ACTIVE));
        notificationDataDTO.setUrgentList(getNotificationsByType(NotificationCategory.NEW));

        return notificationDataDTO;
    }

    private List<NotificationDTO> getNotificationsByType(NotificationCategory status) {
        return mapToListDto(notificationRepository.findByNotificationCategory(status));
    }

    private List<NotificationDTO> mapToListDto(List<Notification> notifications) {
        return notifications.stream()
                .map(notif -> {
                    NotificationDTO dto = mapToDto(notif);
                    dto.setClient(clientService.mapToShortDto(notif.getClient()));
                    dto.setCommentList(commentService.getComments(notif.getContextType().toString(), notif.getContextId()));
                    return dto;
                })
                .toList();
    }

    private NotificationDTO mapToDto(Notification notification) {
        NotificationDTO notificationDTO = new NotificationDTO();

        notificationDTO.setNotificationId(notification.getNotificationId());
        notificationDTO.setMessage(notification.getMessage());
        notificationDTO.setCreateDate(notification.getCreateDate());
        notificationDTO.setUpdateDate(notification.getUpdateDate());
        notificationDTO.setContextType(notification.getContextType().toString());
        notificationDTO.setContextId(notification.getContextId());
        notificationDTO.setNotificationStatus(notification.getNotificationStatus().toString());
        notificationDTO.setNotificationType(notification.getNotificationType().toString());
        notificationDTO.setNotificationCategory(notification.getNotificationCategory().toString());
        return notificationDTO;
    }
}
