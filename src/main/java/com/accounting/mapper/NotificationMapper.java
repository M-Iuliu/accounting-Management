package com.accounting.mapper;

import com.accounting.dto.notification.NotificationDTO;
import com.accounting.entity.Notification;
import com.accounting.entity.enums.ContextType;
import com.accounting.entity.enums.NotificationCategory;
import com.accounting.entity.enums.NotificationStatus;
import com.accounting.entity.enums.NotificationType;
import org.mapstruct.*;

/**
 * MapStruct mapper for Notification entity and DTOs.
 * Provides compile-time safe mapping between entity and DTO representations.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {

    /**
     * Maps Notification entity to NotificationDTO
     * Converts enums to their string representations
     * @param notification the notification entity
     * @return NotificationDTO representation
     */
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "commentList", ignore = true)
    @Mapping(target = "contextType", source = "contextType")
    @Mapping(target = "notificationStatus", source = "notificationStatus")
    @Mapping(target = "notificationType", source = "notificationType")
    @Mapping(target = "notificationCategory", source = "notificationCategory")
    @Mapping(target = "createDate", source = "createdDate")
    @Mapping(target = "updateDate", source = "modifiedDate")
    NotificationDTO toDTO(Notification notification);

    /**
     * Maps ContextType enum to String
     */
    default String mapContextType(ContextType contextType) {
        return contextType != null ? contextType.toString() : null;
    }

    /**
     * Maps NotificationStatus enum to String
     */
    default String mapNotificationStatus(NotificationStatus status) {
        return status != null ? status.toString() : null;
    }

    /**
     * Maps NotificationType enum to String
     */
    default String mapNotificationType(NotificationType type) {
        return type != null ? type.toString() : null;
    }

    /**
     * Maps NotificationCategory enum to String
     */
    default String mapNotificationCategory(NotificationCategory category) {
        return category != null ? category.toString() : null;
    }
}
