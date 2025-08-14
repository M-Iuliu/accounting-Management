package com.accounting.entity;

import com.accounting.entity.enums.NotificationStatus;
import com.accounting.entity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    private String contextType;

    private Long contextId;

    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private String message;

    private Date date;

}
