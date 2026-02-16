package com.accounting.controller;

import com.accounting.dto.client.ClientShortDTO;
import com.accounting.dto.notification.NotificationDTO;
import com.accounting.dto.notification.NotificationDataDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.pagination.PaginationDTO;
import com.accounting.service.notification.NotificationSchedulerService;
import com.accounting.service.notification.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private NotificationSchedulerService schedulerService;

    private NotificationDTO testNotificationDTO;
    private NotificationDataDTO testNotificationDataDTO;
    private PageDTO pageDTO;

    @BeforeEach
    void setUp() {
        ClientShortDTO clientShortDTO = new ClientShortDTO();
        clientShortDTO.setClientId(1L);
        clientShortDTO.setFullName("John Doe");
        clientShortDTO.setTelephone("1234567890");

        testNotificationDTO = new NotificationDTO();
        testNotificationDTO.setNotificationId(1L);
        testNotificationDTO.setClient(clientShortDTO);
        testNotificationDTO.setContextType("RESERVATION");
        testNotificationDTO.setContextId(100L);
        testNotificationDTO.setNotificationStatus("ACTIVE");
        testNotificationDTO.setNotificationCategory("URGENT");
        testNotificationDTO.setNotificationType("PAYMENT_REMINDER");
        testNotificationDTO.setMessage("Payment due for reservation BK001");
        testNotificationDTO.setCreateDate(LocalDate.now());
        testNotificationDTO.setUpdateDate(LocalDate.now());

        NotificationDTO urgentNotification = new NotificationDTO();
        urgentNotification.setNotificationId(1L);
        urgentNotification.setNotificationCategory("URGENT");

        NotificationDTO activeNotification = new NotificationDTO();
        activeNotification.setNotificationId(2L);
        activeNotification.setNotificationCategory("WARNING");

        NotificationDTO archivedNotification = new NotificationDTO();
        archivedNotification.setNotificationId(3L);
        archivedNotification.setNotificationCategory("ARCHIVED");

        testNotificationDataDTO = new NotificationDataDTO();
        testNotificationDataDTO.setUrgentList(List.of(urgentNotification));
        testNotificationDataDTO.setActiveList(List.of(activeNotification));
        testNotificationDataDTO.setArchiveList(List.of(archivedNotification));

        PaginationDTO pagination = new PaginationDTO(1L, 5, List.of(5, 10, 20), 0);
        pageDTO = new PageDTO<>(List.of(testNotificationDTO), pagination);
    }

    @Test
    void testGetNotifications_Success() throws Exception {
        // Arrange
        when(notificationService.getNotificationsCategories()).thenReturn(testNotificationDataDTO);

        // Act & Assert
        mockMvc.perform(get("/notification"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.urgentList", hasSize(1)))
            .andExpect(jsonPath("$.activeList", hasSize(1)))
            .andExpect(jsonPath("$.archiveList", hasSize(1)))
            .andExpect(jsonPath("$.urgentList[0].notificationCategory").value("URGENT"));

        verify(notificationService, times(1)).getNotificationsCategories();
    }

    @Test
    void testGetNotifications_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(notificationService.getNotificationsCategories())
            .thenThrow(new EntityNotFoundException("Notifications not found"));

        // Act & Assert
        mockMvc.perform(get("/notification"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Notifications not found"));

        verify(notificationService, times(1)).getNotificationsCategories();
    }

    @Test
    void testGetNotifications_InternalError_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(notificationService.getNotificationsCategories())
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/notification"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"));

        verify(notificationService, times(1)).getNotificationsCategories();
    }

    @Test
    void testStartCronJobs_Success() throws Exception {
        // Arrange
        doNothing().when(schedulerService).generateNotifications();

        // Act & Assert
        mockMvc.perform(post("/notification"))
            .andExpect(status().isNoContent());

        verify(schedulerService, times(1)).generateNotifications();
    }

    @Test
    void testStartCronJobs_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        doThrow(new EntityNotFoundException("Notifications not found"))
            .when(schedulerService).generateNotifications();

        // Act & Assert
        mockMvc.perform(post("/notification"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Notifications not found"));

        verify(schedulerService, times(1)).generateNotifications();
    }

    @Test
    void testStartCronJobs_InternalError_ReturnsInternalServerError() throws Exception {
        // Arrange
        RuntimeException exception = new RuntimeException("Scheduler error");
        doThrow(exception).when(schedulerService).generateNotifications();

        // Act & Assert
        mockMvc.perform(post("/notification"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value(containsString("An unexpected error occurred")));

        verify(schedulerService, times(1)).generateNotifications();
    }

    @Test
    void testGetNotificationsByContext_Success() throws Exception {
        // Arrange
        List<NotificationDTO> notifications = List.of(testNotificationDTO);
        when(notificationService.getNotificationsByContext("RESERVATION", 100L))
            .thenReturn(notifications);

        // Act & Assert
        mockMvc.perform(get("/notification/RESERVATION/100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].contextType").value("RESERVATION"))
            .andExpect(jsonPath("$[0].contextId").value(100))
            .andExpect(jsonPath("$[0].message").value("Payment due for reservation BK001"));

        verify(notificationService, times(1)).getNotificationsByContext("RESERVATION", 100L);
    }

    @Test
    void testGetNotificationsByContext_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(notificationService.getNotificationsByContext("OFFER", 999L))
            .thenThrow(new EntityNotFoundException("Notification not found"));

        // Act & Assert
        mockMvc.perform(get("/notification/OFFER/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Notification not found for OFFER with id: 999"));

        verify(notificationService, times(1)).getNotificationsByContext("OFFER", 999L);
    }

    @Test
    void testGetNotificationsByContext_InternalError_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(notificationService.getNotificationsByContext("RESERVATION", 100L))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/notification/RESERVATION/100"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"));

        verify(notificationService, times(1)).getNotificationsByContext("RESERVATION", 100L);
    }

    @Test
    void testGetNotificationsByClient_WithoutFilter_Success() throws Exception {
        // Arrange
        when(notificationService.getNotificationsByClient(isNull(), eq(0), eq(5)))
            .thenReturn(pageDTO);

        // Act & Assert
        mockMvc.perform(get("/notification/client")
                .param("page", "0")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items", hasSize(1)))
            .andExpect(jsonPath("$.pagination.totalElements").value(1));

        verify(notificationService, times(1)).getNotificationsByClient(isNull(), eq(0), eq(5));
    }

    @Test
    void testGetNotificationsByClient_WithFilter_Success() throws Exception {
        // Arrange
        when(notificationService.getNotificationsByClient(eq("John"), eq(0), eq(10)))
            .thenReturn(pageDTO);

        // Act & Assert
        mockMvc.perform(get("/notification/client")
                .param("input", "John")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items", hasSize(1)));

        verify(notificationService, times(1)).getNotificationsByClient(eq("John"), eq(0), eq(10));
    }

    @Test
    void testGetNotificationsByClient_DefaultPagination_Success() throws Exception {
        // Arrange
        when(notificationService.getNotificationsByClient(isNull(), eq(0), eq(5)))
            .thenReturn(pageDTO);

        // Act & Assert - Should use default page=0, size=5
        mockMvc.perform(get("/notification/client"))
            .andExpect(status().isOk());

        verify(notificationService, times(1)).getNotificationsByClient(isNull(), eq(0), eq(5));
    }

    @Test
    void testGetNotificationsByClient_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(notificationService.getNotificationsByClient(eq("NonExistent"), eq(0), eq(5)))
            .thenThrow(new EntityNotFoundException("Notification not found"));

        // Act & Assert
        mockMvc.perform(get("/notification/client")
                .param("input", "NonExistent")
                .param("page", "0")
                .param("size", "5"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Notification not found for client searched by filter: [NonExistent]"));

        verify(notificationService, times(1)).getNotificationsByClient(eq("NonExistent"), eq(0), eq(5));
    }

    @Test
    void testGetNotificationsByClient_InternalError_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(notificationService.getNotificationsByClient(isNull(), eq(0), eq(5)))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/notification/client")
                .param("page", "0")
                .param("size", "5"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"));

        verify(notificationService, times(1)).getNotificationsByClient(isNull(), eq(0), eq(5));
    }

    @Test
    void testDismissNotification_Success() throws Exception {
        // Arrange
        String comment = "Resolved the payment issue";
        doNothing().when(notificationService).dismissNotification(1L, comment);

        // Act & Assert
        mockMvc.perform(put("/notification/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comment)))
            .andExpect(status().isNoContent());

        verify(notificationService, times(1)).dismissNotification(eq(1L), anyString());
    }

    @Test
    void testDismissNotification_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        String comment = "Test comment";
        doThrow(new EntityNotFoundException("Notification not found"))
            .when(notificationService).dismissNotification(999L, comment);

        // Act & Assert
        mockMvc.perform(put("/notification/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comment)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Notification not found with id: 999"));

        verify(notificationService, times(1)).dismissNotification(eq(999L), anyString());
    }

    @Test
    void testDismissNotification_InternalError_ReturnsInternalServerError() throws Exception {
        // Arrange
        String comment = "Test comment";
        doThrow(new RuntimeException("Database error"))
            .when(notificationService).dismissNotification(1L, comment);

        // Act & Assert
        mockMvc.perform(put("/notification/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comment)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"));

        verify(notificationService, times(1)).dismissNotification(eq(1L), anyString());
    }

    @Test
    void testDismissNotification_EmptyComment_Success() throws Exception {
        // Arrange
        String comment = "";
        doNothing().when(notificationService).dismissNotification(1L, comment);

        // Act & Assert
        mockMvc.perform(put("/notification/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comment)))
            .andExpect(status().isNoContent());

        verify(notificationService, times(1)).dismissNotification(eq(1L), anyString());
    }
}
