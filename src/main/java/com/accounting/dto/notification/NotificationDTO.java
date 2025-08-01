package com.accounting.dto.notification;

import com.accounting.dto.CommentDTO;
import com.accounting.dto.client.ClientDTO;
import com.accounting.entity.enums.NotificationStatus;
import com.accounting.entity.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDTO {

    private Long commentId;
    private ClientDTO client;

    private String contextType;
    private Long contextId;
    private NotificationStatus notificationStatus;
    private NotificationType notificationType;
    private String message;
    private String time;

    private List<CommentDTO> commentList;

}
