package com.accounting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Message is required")
    @Size(min = 1, max = 5000, message = "Message must be between 1 and 5000 characters")
    private String message;

    private Date date;

    @NotBlank(message = "Context type is required")
    private String contextType; //  'offer' | 'reservation' | 'notification';

    @NotNull(message = "Context ID is required")
    private Long contextId;

    @NotBlank(message = "Comment type is required")
    private String commentType; //  'direct' | 'notification';

    @Size(max = 5000, message = "Reply to must not exceed 5000 characters")
    private String replyTo;  // If it's a reply to a notification
}
