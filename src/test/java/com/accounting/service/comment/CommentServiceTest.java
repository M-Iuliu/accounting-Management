package com.accounting.service.comment;

import com.accounting.dto.CommentDTO;
import com.accounting.entity.Comment;
import com.accounting.entity.Notification;
import com.accounting.entity.enums.CommentType;
import com.accounting.entity.enums.ContextType;
import com.accounting.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentServiceHelper commentServiceHelper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment testComment;
    private CommentDTO testCommentDTO;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testComment = new Comment();
        testComment.setCommentId(1L);
        testComment.setMessage("Test comment message");
        testComment.setContextType(ContextType.OFFER);
        testComment.setContextId(10L);
        testComment.setCommentType(CommentType.DIRECT);
        testComment.setDate(LocalDateTime.now());

        testCommentDTO = new CommentDTO();
        testCommentDTO.setCommentId(1L);
        testCommentDTO.setMessage("Test comment message");
        testCommentDTO.setContextType("OFFER");
        testCommentDTO.setContextId(10L);
        testCommentDTO.setCommentType("DIRECT");

        testNotification = new Notification();
        testNotification.setNotificationId(1L);
        testNotification.setMessage("Notification message");
    }

    @Test
    void testAddComment_Success() {
        // Arrange
        when(commentServiceHelper.mapToEntity(testCommentDTO)).thenReturn(testComment);
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        doNothing().when(commentServiceHelper).validateComment(any(Comment.class));

        // Act
        Comment result = commentService.addComment(testCommentDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testComment.getCommentId(), result.getCommentId());
        assertEquals(testComment.getMessage(), result.getMessage());
        assertNotNull(result.getDate());
        verify(commentServiceHelper, times(1)).mapToEntity(testCommentDTO);
        verify(commentServiceHelper, times(1)).validateComment(any(Comment.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testAddComment_ValidationFails_ThrowsException() {
        // Arrange
        when(commentServiceHelper.mapToEntity(testCommentDTO)).thenReturn(testComment);
        doThrow(new IllegalArgumentException("Validation failed"))
            .when(commentServiceHelper).validateComment(any(Comment.class));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> commentService.addComment(testCommentDTO)
        );
        assertEquals("Validation failed", exception.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void testCreateCommentOnDismissNotification_Success() {
        // Arrange
        String message = "Dismissing this notification";
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(commentServiceHelper).validateComment(any(Comment.class));

        // Act
        Comment result = commentService.createCommentOnDismissNotification(testNotification, message);

        // Assert
        assertNotNull(result);
        assertEquals(message, result.getMessage());
        assertEquals(ContextType.NOTIFICATION, result.getContextType());
        assertEquals(testNotification.getNotificationId(), result.getContextId());
        assertEquals(CommentType.DIRECT, result.getCommentType());
        assertEquals(testNotification.getMessage(), result.getReplyTo());
        assertNotNull(result.getDate());

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment savedComment = commentCaptor.getValue();
        assertEquals(message, savedComment.getMessage());
        verify(commentServiceHelper, times(1)).validateComment(any(Comment.class));
    }

    @Test
    void testGetComments_Success() {
        // Arrange
        List<Comment> comments = List.of(testComment);
        when(commentRepository.findByContextTypeAndContextId(ContextType.OFFER, 10L))
            .thenReturn(comments);
        when(commentServiceHelper.mapToDto(testComment)).thenReturn(testCommentDTO);

        // Act
        List<CommentDTO> result = commentService.getComments("OFFER", 10L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCommentDTO.getCommentId(), result.get(0).getCommentId());
        verify(commentRepository, times(1)).findByContextTypeAndContextId(ContextType.OFFER, 10L);
        verify(commentServiceHelper, times(1)).mapToDto(testComment);
    }

    @Test
    void testGetComments_EmptyList_ReturnsEmpty() {
        // Arrange
        when(commentRepository.findByContextTypeAndContextId(ContextType.RESERVATION, 999L))
            .thenReturn(List.of());

        // Act
        List<CommentDTO> result = commentService.getComments("RESERVATION", 999L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepository, times(1)).findByContextTypeAndContextId(ContextType.RESERVATION, 999L);
    }

    @Test
    void testGetComments_MultipleComments_Success() {
        // Arrange
        Comment comment2 = new Comment();
        comment2.setCommentId(2L);
        comment2.setMessage("Second comment");
        comment2.setContextType(ContextType.OFFER);
        comment2.setContextId(10L);

        CommentDTO commentDTO2 = new CommentDTO();
        commentDTO2.setCommentId(2L);
        commentDTO2.setMessage("Second comment");

        List<Comment> comments = List.of(testComment, comment2);
        when(commentRepository.findByContextTypeAndContextId(ContextType.OFFER, 10L))
            .thenReturn(comments);
        when(commentServiceHelper.mapToDto(testComment)).thenReturn(testCommentDTO);
        when(commentServiceHelper.mapToDto(comment2)).thenReturn(commentDTO2);

        // Act
        List<CommentDTO> result = commentService.getComments("OFFER", 10L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(commentServiceHelper, times(2)).mapToDto(any(Comment.class));
    }
}
