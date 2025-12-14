package com.accounting.controller;

import com.accounting.dto.CommentDTO;
import com.accounting.entity.Comment;
import com.accounting.service.comment.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    private CommentDTO testCommentDTO;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        testCommentDTO = new CommentDTO();
        testCommentDTO.setCommentId(1L);
        testCommentDTO.setMessage("This is a test comment");
        testCommentDTO.setDate(new Date());
        testCommentDTO.setContextType("OFFER");
        testCommentDTO.setContextId(100L);
        testCommentDTO.setCommentType("DIRECT");
        testCommentDTO.setReplyTo(null);

        testComment = new Comment();
        testComment.setCommentId(1L);
    }

    @Test
    void testGetComments_Success() throws Exception {
        // Arrange
        CommentDTO comment1 = new CommentDTO();
        comment1.setCommentId(1L);
        comment1.setMessage("First comment");
        comment1.setContextType("OFFER");
        comment1.setContextId(100L);
        comment1.setCommentType("DIRECT");

        CommentDTO comment2 = new CommentDTO();
        comment2.setCommentId(2L);
        comment2.setMessage("Second comment");
        comment2.setContextType("OFFER");
        comment2.setContextId(100L);
        comment2.setCommentType("DIRECT");

        List<CommentDTO> comments = List.of(comment1, comment2);
        when(commentService.getComments("OFFER", 100L)).thenReturn(comments);

        // Act & Assert
        mockMvc.perform(get("/comment/OFFER/100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].message").value("First comment"))
            .andExpect(jsonPath("$[1].message").value("Second comment"));

        verify(commentService, times(1)).getComments("OFFER", 100L);
    }

    @Test
    void testGetComments_EmptyList_ReturnsEmptyArray() throws Exception {
        // Arrange
        when(commentService.getComments("RESERVATION", 200L)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/comment/RESERVATION/200"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(commentService, times(1)).getComments("RESERVATION", 200L);
    }

    @Test
    void testGetComments_InvalidContextType_ReturnsBadRequest() throws Exception {
        // Arrange
        when(commentService.getComments("INVALID_TYPE", 100L))
            .thenThrow(new IllegalArgumentException("Invalid context type: INVALID_TYPE"));

        // Act & Assert
        mockMvc.perform(get("/comment/INVALID_TYPE/100"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid context type: INVALID_TYPE"));

        verify(commentService, times(1)).getComments("INVALID_TYPE", 100L);
    }

    @Test
    void testGetComments_NotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(commentService.getComments("OFFER", 999L))
            .thenThrow(new EntityNotFoundException("Comment not found"));

        // Act & Assert
        mockMvc.perform(get("/comment/OFFER/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Comment not found"));

        verify(commentService, times(1)).getComments("OFFER", 999L);
    }

    @Test
    void testGetComments_InternalError_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(commentService.getComments("OFFER", 100L))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/comment/OFFER/100"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"));

        verify(commentService, times(1)).getComments("OFFER", 100L);
    }

    @Test
    void testSaveComment_ValidInput_Success() throws Exception {
        // Arrange
        when(commentService.addComment(any(CommentDTO.class))).thenReturn(testComment);

        // Act & Assert
        mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.commentId").value(1));

        verify(commentService, times(1)).addComment(any(CommentDTO.class));
    }

    @Test
    void testSaveComment_InvalidInput_ReturnsBadRequest() throws Exception {
        // Arrange - Create invalid comment (missing required fields)
        CommentDTO invalidComment = new CommentDTO();
        // message, contextType, contextId, and commentType are required but not set

        // Act & Assert
        mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidComment)))
            .andExpect(status().isBadRequest());

        verify(commentService, never()).addComment(any());
    }

    @Test
    void testSaveComment_MissingMessage_ReturnsBadRequest() throws Exception {
        // Arrange
        testCommentDTO.setMessage(null);

        // Act & Assert
        mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());

        verify(commentService, never()).addComment(any());
    }

    @Test
    void testSaveComment_EmptyMessage_ReturnsBadRequest() throws Exception {
        // Arrange
        testCommentDTO.setMessage("");

        // Act & Assert
        mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDTO)))
            .andExpect(status().isBadRequest());

        verify(commentService, never()).addComment(any());
    }

    @Test
    void testSaveComment_MessageTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        String longMessage = "a".repeat(5001); // Exceeds max of 5000
        testCommentDTO.setMessage(longMessage);

        // Act & Assert
        mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDTO)))
            .andExpect(status().isBadRequest());

        verify(commentService, never()).addComment(any());
    }

    @Test
    void testSaveComment_MissingContextType_ReturnsBadRequest() throws Exception {
        // Arrange
        testCommentDTO.setContextType(null);

        // Act & Assert
        mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDTO)))
            .andExpect(status().isBadRequest());

        verify(commentService, never()).addComment(any());
    }

    @Test
    void testSaveComment_MissingContextId_ReturnsBadRequest() throws Exception {
        // Arrange
        testCommentDTO.setContextId(null);

        // Act & Assert
        mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDTO)))
            .andExpect(status().isBadRequest());

        verify(commentService, never()).addComment(any());
    }

    @Test
    void testSaveComment_MissingCommentType_ReturnsBadRequest() throws Exception {
        // Arrange
        testCommentDTO.setCommentType(null);

        // Act & Assert
        mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDTO)))
            .andExpect(status().isBadRequest());

        verify(commentService, never()).addComment(any());
    }

    @Test
    void testSaveComment_WithReplyTo_Success() throws Exception {
        // Arrange
        testCommentDTO.setCommentType("REPLY");
        testCommentDTO.setReplyTo("Original notification message");
        when(commentService.addComment(any(CommentDTO.class))).thenReturn(testComment);

        // Act & Assert
        mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.commentId").value(1));

        verify(commentService, times(1)).addComment(any(CommentDTO.class));
    }

    @Test
    void testSaveComment_ReplyToTooLong_ReturnsBadRequest() throws Exception {
        // Arrange
        String longReplyTo = "a".repeat(5001); // Exceeds max of 5000
        testCommentDTO.setReplyTo(longReplyTo);

        // Act & Assert
        mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDTO)))
            .andExpect(status().isBadRequest());

        verify(commentService, never()).addComment(any());
    }

    @Test
    void testSaveComment_ValidationError_ReturnsBadRequest() throws Exception {
        // Arrange
        when(commentService.addComment(any(CommentDTO.class)))
            .thenThrow(new IllegalArgumentException("Invalid comment data"));

        // Act & Assert
        mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid comment data"));

        verify(commentService, times(1)).addComment(any(CommentDTO.class));
    }

    @Test
    void testSaveComment_InternalError_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(commentService.addComment(any(CommentDTO.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDTO)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("An unexpected error occurred"));

        verify(commentService, times(1)).addComment(any(CommentDTO.class));
    }

    @Test
    void testSaveComment_NotificationContext_Success() throws Exception {
        // Arrange
        testCommentDTO.setContextType("NOTIFICATION");
        testCommentDTO.setContextId(300L);
        when(commentService.addComment(any(CommentDTO.class))).thenReturn(testComment);

        // Act & Assert
        mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.commentId").value(1));

        verify(commentService, times(1)).addComment(any(CommentDTO.class));
    }

    @Test
    void testSaveComment_ReservationContext_Success() throws Exception {
        // Arrange
        testCommentDTO.setContextType("RESERVATION");
        testCommentDTO.setContextId(200L);
        when(commentService.addComment(any(CommentDTO.class))).thenReturn(testComment);

        // Act & Assert
        mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCommentDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.commentId").value(1));

        verify(commentService, times(1)).addComment(any(CommentDTO.class));
    }

    @Test
    void testGetComments_MultipleContextTypes_Success() throws Exception {
        // Test OFFER context
        when(commentService.getComments("OFFER", 100L)).thenReturn(List.of(testCommentDTO));
        mockMvc.perform(get("/comment/OFFER/100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        // Test RESERVATION context
        when(commentService.getComments("RESERVATION", 200L)).thenReturn(List.of(testCommentDTO));
        mockMvc.perform(get("/comment/RESERVATION/200"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        // Test NOTIFICATION context
        when(commentService.getComments("NOTIFICATION", 300L)).thenReturn(List.of(testCommentDTO));
        mockMvc.perform(get("/comment/NOTIFICATION/300"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        verify(commentService, times(1)).getComments("OFFER", 100L);
        verify(commentService, times(1)).getComments("RESERVATION", 200L);
        verify(commentService, times(1)).getComments("NOTIFICATION", 300L);
    }
}
