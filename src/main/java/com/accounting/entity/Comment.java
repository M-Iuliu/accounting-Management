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

    private Date date;

    @Enumerated(EnumType.STRING)
    private ContextType contextType;
    // for which OFFER / RESERVATION / NOTIFICATION is this comment relevant

    private Long contextId;

    @Enumerated(EnumType.STRING)
    private CommentType commentType;
    // if this is a reply comment to a NOTIFICATION ('notification')
    // or if this is a comment of OFFER / RESERVATION ('direct')

    private String replyTo;
}
