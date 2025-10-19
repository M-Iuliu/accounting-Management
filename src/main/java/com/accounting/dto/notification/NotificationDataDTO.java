package com.accounting.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationDataDTO {

    private List<NotificationDTO> urgentList;
    private List<NotificationDTO> activeList;
    private List<NotificationDTO> archiveList;
}
