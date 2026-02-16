package com.accounting.service.comment;

import com.accounting.dto.CommentDTO;
import com.accounting.entity.Comment;
import com.accounting.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Helper component for Comment service operations.
 * Provides utility methods and delegates to CommentMapper for entity-DTO conversions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CommentServiceHelper {

    private final CommentMapper commentMapper;

    /**
     * Maps Comment entity to CommentDTO
     * @param comment the comment entity
     * @return CommentDTO representation
     */
    public CommentDTO mapToDto(Comment comment) {
        log.debug("Mapping Comment entity to DTO for comment ID: {}", comment.getCommentId());
        return commentMapper.toDTO(comment);
    }

    /**
     * Maps CommentDTO to Comment entity
     * @param dto the comment DTO
     * @return Comment entity
     */
    public Comment mapToEntity(CommentDTO dto) {
        log.debug("Mapping CommentDTO to entity for context: {} ID: {}", dto.getContextType(), dto.getContextId());
        return commentMapper.toEntity(dto);
    }

    /**
     * Validates comment business rules before save/update
     * @param comment the comment to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateComment(Comment comment) {
        log.debug("Validating comment: {}", comment.getCommentId());

        if (comment.getMessage() == null || comment.getMessage().isBlank()) {
            throw new IllegalArgumentException("Comment message cannot be empty");
        }

        if (comment.getContextType() == null) {
            throw new IllegalArgumentException("Comment must have a context type");
        }

        if (comment.getContextId() == null) {
            throw new IllegalArgumentException("Comment must have a context ID");
        }

        if (comment.getCommentType() == null) {
            throw new IllegalArgumentException("Comment must have a comment type");
        }

        if (comment.getMessage().length() > 5000) {
            throw new IllegalArgumentException("Comment message must not exceed 5000 characters");
        }

        log.debug("Comment validation successful");
    }
}
