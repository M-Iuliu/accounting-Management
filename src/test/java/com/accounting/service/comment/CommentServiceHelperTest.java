package com.accounting.service.comment;

import com.accounting.dto.CommentDTO;
import com.accounting.entity.Comment;
import com.accounting.entity.enums.CommentType;
import com.accounting.entity.enums.ContextType;
import com.accounting.mapper.CommentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceHelperTest {

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceHelper commentServiceHelper;

    private Comment testComment;
    private CommentDTO testCommentDTO;

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
    }

    @Test
    void testMapToDto_Success() {
        // Arrange
        when(commentMapper.toDTO(testComment)).thenReturn(testCommentDTO);

        // Act
        CommentDTO result = commentServiceHelper.mapToDto(testComment);

        // Assert
        assertNotNull(result);
        assertEquals(testCommentDTO.getCommentId(), result.getCommentId());
        assertEquals(testCommentDTO.getMessage(), result.getMessage());
        verify(commentMapper, times(1)).toDTO(testComment);
    }

    @Test
    void testMapToEntity_Success() {
        // Arrange
        when(commentMapper.toEntity(testCommentDTO)).thenReturn(testComment);

        // Act
        Comment result = commentServiceHelper.mapToEntity(testCommentDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testComment.getCommentId(), result.getCommentId());
        assertEquals(testComment.getMessage(), result.getMessage());
        verify(commentMapper, times(1)).toEntity(testCommentDTO);
    }

    @Test
    void testValidateComment_Success() {
        // Act & Assert
        assertDoesNotThrow(() -> commentServiceHelper.validateComment(testComment));
    }

    @Test
    void testValidateComment_NullMessage_ThrowsException() {
        // Arrange
        testComment.setMessage(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> commentServiceHelper.validateComment(testComment)
        );
        assertEquals("Comment message cannot be empty", exception.getMessage());
    }

    @Test
    void testValidateComment_BlankMessage_ThrowsException() {
        // Arrange
        testComment.setMessage("   ");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> commentServiceHelper.validateComment(testComment)
        );
        assertEquals("Comment message cannot be empty", exception.getMessage());
    }

    @Test
    void testValidateComment_EmptyMessage_ThrowsException() {
        // Arrange
        testComment.setMessage("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> commentServiceHelper.validateComment(testComment)
        );
        assertEquals("Comment message cannot be empty", exception.getMessage());
    }

    @Test
    void testValidateComment_NullContextType_ThrowsException() {
        // Arrange
        testComment.setContextType(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> commentServiceHelper.validateComment(testComment)
        );
        assertEquals("Comment must have a context type", exception.getMessage());
    }

    @Test
    void testValidateComment_NullContextId_ThrowsException() {
        // Arrange
        testComment.setContextId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> commentServiceHelper.validateComment(testComment)
        );
        assertEquals("Comment must have a context ID", exception.getMessage());
    }

    @Test
    void testValidateComment_NullCommentType_ThrowsException() {
        // Arrange
        testComment.setCommentType(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> commentServiceHelper.validateComment(testComment)
        );
        assertEquals("Comment must have a comment type", exception.getMessage());
    }

    @Test
    void testValidateComment_MessageTooLong_ThrowsException() {
        // Arrange
        String longMessage = "x".repeat(5001);
        testComment.setMessage(longMessage);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> commentServiceHelper.validateComment(testComment)
        );
        assertEquals("Comment message must not exceed 5000 characters", exception.getMessage());
    }

    @Test
    void testValidateComment_MessageExactly5000Characters_Success() {
        // Arrange
        String maxMessage = "x".repeat(5000);
        testComment.setMessage(maxMessage);

        // Act & Assert
        assertDoesNotThrow(() -> commentServiceHelper.validateComment(testComment));
    }
}
