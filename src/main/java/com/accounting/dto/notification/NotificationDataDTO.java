package com.accounting.dto.notification;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationDataDTO {

    private List<NotificationDTO> urgentList;
    private List<NotificationDTO> activeList;
    private List<NotificationDTO> historyList;
}
