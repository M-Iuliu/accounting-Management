package com.accounting.repository;

import com.accounting.entity.Notification;
import com.accounting.entity.enums.ContextType;
import com.accounting.entity.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByContextTypeAndContextId(ContextType contextType, Long contextId);

    List<Notification> findByNotificationType(NotificationType status);

    boolean existsByContextTypeAndContextIdAndNotificationType(
            String contextType,
            Long contextId,
            NotificationType notificationType
    );


    @Query("SELECT n FROM Notification n WHERE n.updateDate = :targetDate AND n.notificationType = :notifType")
    List<Notification> findNotificationToArchive(@Param("targetDate") Date targetDate,
                                                 @Param("notifType") NotificationType notifType);

}