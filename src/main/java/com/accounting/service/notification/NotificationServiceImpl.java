package com.accounting.service.notification;


import com.accounting.dto.notification.NotificationDTO;
import com.accounting.dto.notification.NotificationDataDTO;
import com.accounting.entity.Client;
import com.accounting.entity.Notification;
import com.accounting.entity.enums.ContextType;
import com.accounting.entity.enums.NotificationStatus;
import com.accounting.entity.enums.NotificationType;
import com.accounting.repository.NotificationRepository;
import com.accounting.service.clients.ClientService;
import com.accounting.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ClientService clientService;
    private final CommentService commentService;

    public Notification createNotification(Client client, String message, ContextType contextType, Long contextId) {
        Notification notification = new Notification();
        notification.setClient(client);
        notification.setMessage(message);
        notification.setDate(new Date());
        notification.setContextType(contextType);
        notification.setContextId(contextId);
        notification.setNotificationType(NotificationType.NEW);
        notification.setNotificationStatus(NotificationStatus.UNSEEN);

        return notificationRepository.save(notification);
    }

    public List<NotificationDTO> getNotificationsByContext(String contextType, Long contextId) {
        return mapToListDto(
                notificationRepository.findByContextTypeAndContextId(
                        ContextType.valueOf(contextType.toUpperCase()),
                        contextId));
    }

    public NotificationDataDTO getNotificationsCategories() {
        NotificationDataDTO notificationDataDTO = new NotificationDataDTO();

        notificationDataDTO.setArchiveList(getNotificationsByType(NotificationType.ARCHIVED));
        notificationDataDTO.setActiveList(getNotificationsByType(NotificationType.ACTIVE));
        notificationDataDTO.setUrgentList(getNotificationsByType(NotificationType.NEW));

        return notificationDataDTO;
    }


    private List<NotificationDTO> getNotificationsByType(NotificationType status) {
        return mapToListDto(notificationRepository.findByNotificationType(status));
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
        notificationDTO.setDate(notification.getDate());
        notificationDTO.setContextType(notification.getContextType().toString());
        notificationDTO.setContextId(notification.getContextId());
        notificationDTO.setNotificationStatus(notification.getNotificationStatus().toString());
        notificationDTO.setNotificationType(notification.getNotificationType().toString());
        return notificationDTO;
    }
}
