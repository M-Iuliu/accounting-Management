package com.accounting.entity;

import com.accounting.entity.enums.CommentType;
import com.accounting.entity.enums.ContextType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a comment on an offer, reservation, or notification.
 * Supports both direct comments and replies to notifications.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Comment extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "date")
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name = "context_type", length = 50, nullable = false)
    private ContextType contextType;

    @Column(name = "context_id", nullable = false)
    private Long contextId;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type", length = 50, nullable = false)
    private CommentType commentType;

    @Column(name = "reply_to", columnDefinition = "TEXT")
    private String replyTo;
}
