package com.accounting.entity;

import com.accounting.entity.enums.NotificationStatus;
import com.accounting.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    private String contextType;
    private Long contextId;
    private NotificationStatus notificationStatus;
    private NotificationType notificationType;
    private String message;
    private String date;

}
