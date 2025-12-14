package com.accounting.entity;

import com.accounting.entity.enums.ContextType;
import com.accounting.entity.enums.NotificationCategory;
import com.accounting.entity.enums.NotificationStatus;
import com.accounting.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "client")
@EqualsAndHashCode(callSuper = false)
public class Notification extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(name = "context_type", length = 50)
    private ContextType contextType;

    @Column(name = "context_id")
    private Long contextId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private NotificationStatus notificationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 50)
    private NotificationCategory notificationCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50)
    private NotificationType notificationType;

    @Column(columnDefinition = "TEXT")
    private String message;

}
