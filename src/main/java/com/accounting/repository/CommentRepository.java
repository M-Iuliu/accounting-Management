package com.accounting.repository;

import com.accounting.entity.Comment;
import com.accounting.entity.enums.ContextType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByContextTypeAndContextId(ContextType contextType, Long contextId);
}