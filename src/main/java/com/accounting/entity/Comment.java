package com.accounting.entity;

import com.accounting.entity.enums.CommentType;
import com.accounting.entity.enums.ContextType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private String message;
    private Date date; // ISO date string like "2025-04-06"
    private ContextType contextType; //  'offer' | 'reservation' | 'notification';
    private Long contextId;
    private CommentType commentType; //  'direct' | 'notification';
    private String replyTo;
}
