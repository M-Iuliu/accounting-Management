package com.accounting.entity;

import com.accounting.entity.enums.ContextType;
import com.accounting.entity.enums.NotificationCategory;
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

    @Enumerated(EnumType.STRING)
    private ContextType contextType; // for which OFFER / RESERVATION is this notification relevant

    private Long contextId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private NotificationStatus notificationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private NotificationCategory notificationCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private NotificationType notificationType;

    private String message;

    private Date createDate;

    private Date updateDate;

}
