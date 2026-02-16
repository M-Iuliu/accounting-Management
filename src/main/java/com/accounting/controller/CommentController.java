package com.accounting.controller;

import com.accounting.dto.CommentDTO;
import com.accounting.entity.Comment;
import com.accounting.exeption.ErrorResponse;
import com.accounting.service.comment.CommentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for comment operations.
 * Handles creation and retrieval of comments for offers, reservations, and notifications.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * Gets all comments for a specific context
     * @param contextType the context type (offer, reservation, or notification)
     * @param contextId the context ID
     * @return list of comments
     */
    @GetMapping("/{contextType}/{contextId}")
    public ResponseEntity<?> getComments(@PathVariable String contextType, @PathVariable Long contextId) {
        log.info("GET request - get comments for context: {} ID: {}", contextType, contextId);

        try {
            List<CommentDTO> comments = commentService.getComments(contextType, contextId);
            log.info("Successfully retrieved {} comments for context {} ID {}", comments.size(), contextType, contextId);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid context type: {}", contextType, e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid context type: " + contextType, HttpStatus.BAD_REQUEST.value()));
        } catch (EntityNotFoundException e) {
            log.warn("Comments not found for context {} ID {}", contextType, contextId);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Comment not found", HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            log.error("Unexpected error retrieving comments for context {} ID {}: {}",
                contextType, contextId, e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Creates a new comment
     * @param commentDTO the comment data
     * @return the created comment entity
     */
    @PostMapping()
    public ResponseEntity<?> saveComment(@Valid @RequestBody CommentDTO commentDTO) {
        log.info("POST request - create comment for context: {} ID: {}",
            commentDTO.getContextType(), commentDTO.getContextId());

        try {
            Comment comment = commentService.addComment(commentDTO);
            log.info("Successfully created comment with ID: {}", comment.getCommentId());
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (IllegalArgumentException e) {
            log.warn("Validation error creating comment: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Unexpected error creating comment: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Exception handler for validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation error: {}", errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST.value()));
    }
}
