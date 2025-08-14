package com.accounting.entity;

import com.accounting.entity.enums.CommentType;
import com.accounting.entity.enums.ContextType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private String message;

    private Date date; // ISO date string like "2025-04-06"

    @Enumerated(EnumType.STRING)
    private ContextType contextType; //  'offer' | 'reservation' | 'notification';

    private Long contextId;

    @Enumerated(EnumType.STRING)
    private CommentType commentType; //  'direct' | 'notification';

    private String replyTo;
}
