package com.accounting.dto.notification;

import com.accounting.dto.CommentDTO;
import com.accounting.dto.client.ClientShortDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDTO {
    private Long notificationId;
    private ClientShortDTO client;
    private String contextType;
    private Long contextId;
    private String notificationStatus;
    private String notificationType;
    private String notificationCategory;
    private String message;
    private LocalDate createDate;
    private LocalDate updateDate;
    private List<CommentDTO> commentList;
}
