package com.accounting.service.comment;

import com.accounting.dto.CommentDTO;
import com.accounting.entity.Comment;
import com.accounting.entity.Notification;
import com.accounting.entity.enums.CommentType;
import com.accounting.entity.enums.ContextType;
import com.accounting.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation for Comment domain operations.
 * Handles business logic, transaction management, and orchestrates
 * interactions between controller, repository, and mapper layers.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentServiceHelper commentServiceHelper;

    /**
     * Adds a new comment from DTO
     * @param dto the comment DTO
     * @return the created comment entity
     */
    @Override
    @Transactional
    public Comment addComment(CommentDTO dto) {
        log.info("Adding comment for context: {} ID: {}, type: {}",
            dto.getContextType(), dto.getContextId(), dto.getCommentType());

        Comment comment = commentServiceHelper.mapToEntity(dto);
        comment.setDate(LocalDateTime.now());

        commentServiceHelper.validateComment(comment);
        comment = commentRepository.save(comment);

        log.info("Successfully created comment with ID: {}", comment.getCommentId());
        return comment;
    }

    /**
     * Creates a comment when dismissing a notification
     * @param notification the notification being dismissed
     * @param message the comment message
     * @return the created comment entity
     */
    @Override
    @Transactional
    public Comment createCommentOnDismissNotification(Notification notification, String message) {
        log.info("Creating comment on dismiss notification ID: {}", notification.getNotificationId());

        Comment comment = new Comment();
        comment.setMessage(message);
        comment.setDate(LocalDateTime.now());
        comment.setContextType(ContextType.NOTIFICATION);
        comment.setContextId(notification.getNotificationId());
        comment.setCommentType(CommentType.DIRECT);
        comment.setReplyTo(notification.getMessage());

        commentServiceHelper.validateComment(comment);
        comment = commentRepository.save(comment);

        log.info("Successfully created comment with ID: {} for notification {}",
            comment.getCommentId(), notification.getNotificationId());
        return comment;
    }

    /**
     * Gets all comments for a specific context
     * @param contextType the context type (OFFER, RESERVATION, or NOTIFICATION)
     * @param contextId the context ID
     * @return list of comment DTOs
     */
    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getComments(String contextType, Long contextId) {
        log.info("Getting comments for context: {} ID: {}", contextType, contextId);

        ContextType context = ContextType.valueOf(contextType.toUpperCase());
        List<Comment> comments = commentRepository.findByContextTypeAndContextId(context, contextId);

        List<CommentDTO> commentDTOs = comments.stream()
                .map(commentServiceHelper::mapToDto)
                .toList();

        log.info("Found {} comments for context {} ID {}", commentDTOs.size(), contextType, contextId);
        return commentDTOs;
    }
}
