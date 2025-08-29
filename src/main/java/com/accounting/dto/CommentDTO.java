package com.accounting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDTO {

    private Long commentId;
    private String message;
    private Date date;
    private String contextType; //  'offer' | 'reservation' | 'notification';
    private Long contextId;
    private String commentType; //  'direct' | 'notification';
    private String replyTo;  // If it's a reply to a notification
}
