package com.accounting.controller;

import com.accounting.dto.notification.NotificationDTO;
import com.accounting.dto.notification.NotificationDataDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.exeption.ErrorResponse;
import com.accounting.service.notification.NotificationSchedulerService;
import com.accounting.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/notification")
@AllArgsConstructor
public class NotificationController {

    @Autowired
    private final NotificationService notificationService;

    @Autowired
    private final NotificationSchedulerService schedulerService;

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

    @PostMapping()
    public ResponseEntity<?> startCronJobs() {
        try {
            schedulerService.generateNotifications();
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Notifications not found", HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(String.format("An unexpected error occurred: [%s]", e), HttpStatus.INTERNAL_SERVER_ERROR.value()));
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

    @GetMapping("/client")
    public ResponseEntity<?> getNotificationsByClient(
            @RequestParam(required = false) String input,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            PageDTO notifications = notificationService.getNotificationsByClient(input, page, size);
            return ResponseEntity.ok(notifications);
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            String.format("Notification not found for client searched by filter: [%s]", input),
                            HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    } //TODO: Check & test endpoint

    @PutMapping("/{notificationId}")
    public ResponseEntity<?> dismissNotification(@PathVariable Long notificationId, @RequestBody String comment) {
        try {
            notificationService.dismissNotification(notificationId, comment);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            String.format("Notification not found with id: %s", notificationId),
                            HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST.value()));
    }
}
