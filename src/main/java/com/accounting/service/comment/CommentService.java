package com.accounting.service.comment;

import com.accounting.entity.Comment;
import com.accounting.dto.CommentDTO;
import com.accounting.entity.enums.ContextType;

import java.util.List;

public interface CommentService {
    Comment addComment(CommentDTO dto);
    List<CommentDTO> getComments(ContextType contextType, Long contextId);
}
