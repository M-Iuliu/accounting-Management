package com.accounting.service.notification;

import com.accounting.dto.CommentDTO;
import com.accounting.dto.client.ClientShortDTO;
import com.accounting.dto.notification.NotificationDTO;
import com.accounting.dto.notification.NotificationDataDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.entity.Client;
import com.accounting.entity.Notification;
import com.accounting.entity.enums.ContextType;
import com.accounting.entity.enums.NotificationCategory;
import com.accounting.entity.enums.NotificationStatus;
import com.accounting.entity.enums.NotificationType;
import com.accounting.repository.NotificationRepository;
import com.accounting.service.clients.ClientService;
import com.accounting.service.comment.CommentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ClientService clientService;

    @Mock
    private CommentService commentService;

    @Mock
    private NotificationServiceHelper notificationServiceHelper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification testNotification;
    private NotificationDTO testNotificationDTO;
    private Client testClient;
    private ClientShortDTO testClientShortDTO;
    private List<CommentDTO> testComments;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setClientId(1L);
        testClient.setName("John");
        testClient.setSurname("Doe");

        testClientShortDTO = new ClientShortDTO();
        testClientShortDTO.setClientId(1L);
        testClientShortDTO.setFullName("John Doe");

        testNotification = new Notification();
        testNotification.setNotificationId(1L);
        testNotification.setClient(testClient);
        testNotification.setMessage("Test notification message");
        testNotification.setContextType(ContextType.RESERVATION);
        testNotification.setContextId(100L);
        testNotification.setNotificationCategory(NotificationCategory.NEW);
        testNotification.setNotificationStatus(NotificationStatus.UNSEEN);
        testNotification.setNotificationType(NotificationType.RESERVATION_PAYMENT);

        testNotificationDTO = new NotificationDTO();
        testNotificationDTO.setNotificationId(1L);
        testNotificationDTO.setMessage("Test notification message");

        testComments = new ArrayList<>();
        CommentDTO comment1 = new CommentDTO();
        comment1.setMessage("Comment 1");
        testComments.add(comment1);
    }

    @Test
    void testCreateNotification_Success() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(notificationServiceHelper).validateNotification(any(Notification.class));

        // Act
        Notification result = notificationService.createNotification(
            testClient,
            "New notification",
            ContextType.OFFER,
            50L,
            NotificationType.OFFER
        );

        // Assert
        assertNotNull(result);
        assertEquals(testClient, result.getClient());
        assertEquals("New notification", result.getMessage());
        assertEquals(ContextType.OFFER, result.getContextType());
        assertEquals(50L, result.getContextId());
        assertEquals(NotificationCategory.NEW, result.getNotificationCategory());
        assertEquals(NotificationStatus.UNSEEN, result.getNotificationStatus());
        assertEquals(NotificationType.OFFER, result.getNotificationType());
        verify(notificationServiceHelper, times(1)).validateNotification(any(Notification.class));
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testCreateNotification_ValidationFails_ThrowsException() {
        // Arrange
        doThrow(new IllegalArgumentException("Validation failed"))
            .when(notificationServiceHelper).validateNotification(any(Notification.class));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> notificationService.createNotification(
                testClient, "Test", ContextType.OFFER, 1L, NotificationType.OFFER)
        );
        assertEquals("Validation failed", exception.getMessage());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testDismissNotification_FromNewToActive_Success() {
        // Arrange
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(testNotification)).thenReturn(testNotification);
        doNothing().when(commentService).createCommentOnDismissNotification(any(Notification.class), anyString());

        // Act
        notificationService.dismissNotification(1L, "Dismissing notification");

        // Assert
        assertEquals(NotificationCategory.ACTIVE, testNotification.getNotificationCategory());
        assertEquals(NotificationStatus.SEEN, testNotification.getNotificationStatus());
        verify(notificationRepository, times(1)).findById(1L);
        verify(commentService, times(1)).createCommentOnDismissNotification(testNotification, "Dismissing notification");
        verify(notificationRepository, times(1)).save(testNotification);
    }

    @Test
    void testDismissNotification_FromActiveToArchived_Success() {
        // Arrange
        testNotification.setNotificationCategory(NotificationCategory.ACTIVE);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(testNotification)).thenReturn(testNotification);

        // Act
        notificationService.dismissNotification(1L, null);

        // Assert
        assertEquals(NotificationCategory.ARCHIVED, testNotification.getNotificationCategory());
        verify(notificationRepository, times(1)).findById(1L);
        verify(commentService, never()).createCommentOnDismissNotification(any(), anyString());
        verify(notificationRepository, times(1)).save(testNotification);
    }

    @Test
    void testDismissNotification_WithBlankMessage_NoCommentAdded() {
        // Arrange
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(testNotification)).thenReturn(testNotification);

        // Act
        notificationService.dismissNotification(1L, "   ");

        // Assert
        assertEquals(NotificationCategory.ACTIVE, testNotification.getNotificationCategory());
        verify(commentService, never()).createCommentOnDismissNotification(any(), anyString());
        verify(notificationRepository, times(1)).save(testNotification);
    }

    @Test
    void testDismissNotification_NotFound_ThrowsException() {
        // Arrange
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> notificationService.dismissNotification(999L, null)
        );
        assertEquals("No notification found for notificationId: 999", exception.getMessage());
        verify(notificationRepository, times(1)).findById(999L);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testGetNotificationsByContext_Success() {
        // Arrange
        List<Notification> notifications = List.of(testNotification);
        when(notificationRepository.findByContextTypeAndContextId(ContextType.RESERVATION, 100L))
            .thenReturn(notifications);
        when(notificationServiceHelper.mapToDto(testNotification)).thenReturn(testNotificationDTO);
        when(clientService.mapToShortDto(testClient)).thenReturn(testClientShortDTO);
        when(commentService.getComments(anyString(), any())).thenReturn(testComments);

        // Act
        List<NotificationDTO> result = notificationService.getNotificationsByContext("RESERVATION", 100L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(notificationRepository, times(1)).findByContextTypeAndContextId(ContextType.RESERVATION, 100L);
    }

    @Test
    void testGetNotificationsByClient_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> notificationPage = new PageImpl<>(List.of(testNotification), pageable, 1);

        when(notificationRepository.findByClientNameOrPhone(eq("John"), any(Pageable.class)))
            .thenReturn(notificationPage);
        when(notificationServiceHelper.mapToDto(testNotification)).thenReturn(testNotificationDTO);
        when(clientService.mapToShortDto(testClient)).thenReturn(testClientShortDTO);
        when(commentService.getComments(anyString(), any())).thenReturn(testComments);

        // Act
        PageDTO result = notificationService.getNotificationsByClient("John", 0, 10);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, ((List<?>) result.getItems()).size());
        verify(notificationRepository, times(1)).findByClientNameOrPhone(eq("John"), any(Pageable.class));
    }

    @Test
    void testGetNotificationsCategories_Success() {
        // Arrange
        Notification urgentNotification = new Notification();
        urgentNotification.setNotificationId(1L);
        urgentNotification.setClient(testClient);
        urgentNotification.setNotificationCategory(NotificationCategory.NEW);
        urgentNotification.setContextType(ContextType.OFFER);
        urgentNotification.setContextId(1L);

        Notification activeNotification = new Notification();
        activeNotification.setNotificationId(2L);
        activeNotification.setClient(testClient);
        activeNotification.setNotificationCategory(NotificationCategory.ACTIVE);
        activeNotification.setContextType(ContextType.RESERVATION);
        activeNotification.setContextId(2L);

        Notification archivedNotification = new Notification();
        archivedNotification.setNotificationId(3L);
        archivedNotification.setClient(testClient);
        archivedNotification.setNotificationCategory(NotificationCategory.ARCHIVED);
        archivedNotification.setContextType(ContextType.OFFER);
        archivedNotification.setContextId(3L);

        when(notificationRepository.findByNotificationCategory(NotificationCategory.NEW))
            .thenReturn(List.of(urgentNotification));
        when(notificationRepository.findByNotificationCategory(NotificationCategory.ACTIVE))
            .thenReturn(List.of(activeNotification));
        when(notificationRepository.findByNotificationCategory(NotificationCategory.ARCHIVED))
            .thenReturn(List.of(archivedNotification));

        when(notificationServiceHelper.mapToDto(any())).thenReturn(testNotificationDTO);
        when(clientService.mapToShortDto(any())).thenReturn(testClientShortDTO);
        when(commentService.getComments(anyString(), any())).thenReturn(testComments);

        // Act
        NotificationDataDTO result = notificationService.getNotificationsCategories();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getUrgentList());
        assertNotNull(result.getActiveList());
        assertNotNull(result.getArchiveList());
        assertEquals(1, result.getUrgentList().size());
        assertEquals(1, result.getActiveList().size());
        assertEquals(1, result.getArchiveList().size());
        verify(notificationRepository, times(1)).findByNotificationCategory(NotificationCategory.NEW);
        verify(notificationRepository, times(1)).findByNotificationCategory(NotificationCategory.ACTIVE);
        verify(notificationRepository, times(1)).findByNotificationCategory(NotificationCategory.ARCHIVED);
    }
}
