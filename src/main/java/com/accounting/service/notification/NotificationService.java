package com.accounting.service.notification;

import com.accounting.dto.notification.NotificationDTO;
import com.accounting.dto.notification.NotificationDataDTO;
import com.accounting.entity.Client;
import com.accounting.entity.Notification;
import com.accounting.entity.enums.ContextType;

import java.util.List;

public interface NotificationService {

    Notification createNotification(Client client, String message, ContextType contextType, Long contextId);

    List<NotificationDTO> getNotificationsByContext(String contextType, Long contextId);

    NotificationDataDTO getNotificationsCategories();
}
