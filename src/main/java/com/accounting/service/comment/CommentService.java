package com.accounting.service.comment;

import com.accounting.dto.CommentDTO;
import com.accounting.entity.Comment;
import com.accounting.entity.Notification;

import java.util.List;

public interface CommentService {

    Comment addComment(CommentDTO dto);

    List<CommentDTO> getComments(String contextType, Long contextId);

    Comment createCommentOnDismissNotification(Notification notification, String message);
}
