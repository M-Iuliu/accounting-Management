package com.accounting.controller;

import com.accounting.dto.notification.NotificationDTO;
import com.accounting.dto.notification.NotificationDataDTO;
import com.accounting.exeption.ErrorResponse;
import com.accounting.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/notification")
@AllArgsConstructor
public class NotificationController {

    @Autowired
    private final NotificationService notificationService;

    @GetMapping()
    public ResponseEntity<?> getNotifications() {
        try {
            NotificationDataDTO notifications = notificationService.getNotificationsCategories();
            return ResponseEntity.ok(notifications);
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Notifications not found", HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/{contextType}/{contextId}")
    public ResponseEntity<?> getNotificationsByContext(@PathVariable String contextType, @PathVariable Long contextId) {
        try {
            List<NotificationDTO> notifications = notificationService.getNotificationsByContext(contextType, contextId);
            return ResponseEntity.ok(notifications);
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            String.format("Notification not found for %s with id: %s", contextType, contextId),
                            HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    //TODO: PUT -> Dismiss notification
    //              - if status == NotificationType.NEW,
    //                  then change to ACTIVE
    //                          and set NotificationStatus.SEEN
    //              - if status == NotificationType.ACTIVE,
    //                  then change to ARCHIVED

    //TODO: GET -> search notification by client name / telephone, filtered by given NotificationType

}
