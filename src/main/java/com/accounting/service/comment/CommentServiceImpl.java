package com.accounting.service.comment;


import com.accounting.dto.CommentDTO;
import com.accounting.entity.Comment;
import com.accounting.entity.enums.CommentType;
import com.accounting.entity.enums.ContextType;
import com.accounting.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment addComment(CommentDTO dto) {
        Comment comment = new Comment();
        comment.setMessage(dto.getMessage());
        comment.setDate(new Date());
        comment.setContextType(ContextType.valueOf(dto.getContextType()));
        comment.setContextId(dto.getContextId());
        comment.setCommentType(CommentType.valueOf(dto.getCommentType()));
        comment.setReplyTo(dto.getReplyTo() != null ? dto.getReplyTo() : null);

        return commentRepository.save(comment);
    }

    public List<CommentDTO> getComments(String contextType, Long contextId) {
        return commentRepository.findByContextTypeAndContextId(ContextType.valueOf(contextType.toUpperCase()), contextId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }


    private CommentDTO mapToDto(Comment comment) {
        return new CommentDTO(
                comment.getCommentId(),
                comment.getMessage(),
                comment.getDate(),
                comment.getContextType().toString(),
                comment.getContextId(),
                comment.getCommentType().toString(),
                comment.getReplyTo()
        );
    }
}
