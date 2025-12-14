package com.accounting.repository;

import com.accounting.entity.Comment;
import com.accounting.entity.enums.CommentType;
import com.accounting.entity.enums.ContextType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for CommentRepository using real database operations.
 */
@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Comment comment1;
    private Comment comment2;
    private Comment comment3;
    private Comment comment4;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        commentRepository.deleteAll();

        // Create comment 1 - Direct comment on offer
        comment1 = new Comment();
        comment1.setMessage("This is a great offer for Paris");
        comment1.setDate(LocalDateTime.now().minusDays(2));
        comment1.setContextType(ContextType.OFFER);
        comment1.setContextId(100L);
        comment1.setCommentType(CommentType.DIRECT);
        comment1.setReplyTo(null);

        // Create comment 2 - Another direct comment on same offer
        comment2 = new Comment();
        comment2.setMessage("Customer requested additional details");
        comment2.setDate(LocalDateTime.now().minusDays(1));
        comment2.setContextType(ContextType.OFFER);
        comment2.setContextId(100L);
        comment2.setCommentType(CommentType.DIRECT);
        comment2.setReplyTo(null);

        // Create comment 3 - Comment on reservation
        comment3 = new Comment();
        comment3.setMessage("Payment confirmed");
        comment3.setDate(LocalDateTime.now().minusDays(3));
        comment3.setContextType(ContextType.RESERVATION);
        comment3.setContextId(200L);
        comment3.setCommentType(CommentType.DIRECT);
        comment3.setReplyTo(null);

        // Create comment 4 - Reply to notification
        comment4 = new Comment();
        comment4.setMessage("Issue has been resolved");
        comment4.setDate(LocalDateTime.now());
        comment4.setContextType(ContextType.NOTIFICATION);
        comment4.setContextId(300L);
        comment4.setCommentType(CommentType.NOTIFICATION);
        comment4.setReplyTo("Original notification message");

        // Persist test data
        entityManager.persist(comment1);
        entityManager.persist(comment2);
        entityManager.persist(comment3);
        entityManager.persist(comment4);
        entityManager.flush();
    }

    @Test
    void testFindByContextTypeAndContextId_ReturnsMatchingComments() {
        // Act
        List<Comment> result = commentRepository.findByContextTypeAndContextId(
            ContextType.OFFER, 100L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getMessage().equals("This is a great offer for Paris")));
        assertTrue(result.stream().anyMatch(c -> c.getMessage().equals("Customer requested additional details")));
    }

    @Test
    void testFindByContextTypeAndContextId_SingleComment_ReturnsOne() {
        // Act
        List<Comment> result = commentRepository.findByContextTypeAndContextId(
            ContextType.RESERVATION, 200L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Payment confirmed", result.get(0).getMessage());
        assertEquals(CommentType.DIRECT, result.get(0).getCommentType());
    }

    @Test
    void testFindByContextTypeAndContextId_NoMatch_ReturnsEmpty() {
        // Act
        List<Comment> result = commentRepository.findByContextTypeAndContextId(
            ContextType.OFFER, 999L);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testFindByContextTypeAndContextId_NotificationContext_ReturnsComment() {
        // Act
        List<Comment> result = commentRepository.findByContextTypeAndContextId(
            ContextType.NOTIFICATION, 300L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Issue has been resolved", result.get(0).getMessage());
        assertEquals(CommentType.NOTIFICATION, result.get(0).getCommentType());
        assertEquals("Original notification message", result.get(0).getReplyTo());
    }

    @Test
    void testFindByContextTypeAndContextId_DifferentContextType_ReturnsEmpty() {
        // Act - Search for offer context type with reservation ID
        List<Comment> result = commentRepository.findByContextTypeAndContextId(
            ContextType.OFFER, 200L);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testFindById_ReturnsComment() {
        // Act
        Comment found = commentRepository.findById(comment1.getCommentId()).orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals(comment1.getCommentId(), found.getCommentId());
        assertEquals("This is a great offer for Paris", found.getMessage());
        assertEquals(ContextType.OFFER, found.getContextType());
        assertEquals(100L, found.getContextId());
    }

    @Test
    void testSaveComment_Success() {
        // Arrange
        Comment newComment = new Comment();
        newComment.setMessage("New comment on reservation");
        newComment.setDate(LocalDateTime.now());
        newComment.setContextType(ContextType.RESERVATION);
        newComment.setContextId(200L);
        newComment.setCommentType(CommentType.DIRECT);
        newComment.setReplyTo(null);

        // Act
        Comment saved = commentRepository.save(newComment);

        // Assert
        assertNotNull(saved.getCommentId());
        assertEquals("New comment on reservation", saved.getMessage());

        // Verify it can be found
        Comment found = commentRepository.findById(saved.getCommentId()).orElse(null);
        assertNotNull(found);
        assertEquals("New comment on reservation", found.getMessage());

        // Verify it's included in context comments
        List<Comment> contextComments = commentRepository.findByContextTypeAndContextId(
            ContextType.RESERVATION, 200L);
        assertEquals(2, contextComments.size());
    }

    @Test
    void testSaveComment_WithReply_Success() {
        // Arrange
        Comment replyComment = new Comment();
        replyComment.setMessage("This is a reply");
        replyComment.setDate(LocalDateTime.now());
        replyComment.setContextType(ContextType.NOTIFICATION);
        replyComment.setContextId(300L);
        replyComment.setCommentType(CommentType.NOTIFICATION);
        replyComment.setReplyTo("Previous notification");

        // Act
        Comment saved = commentRepository.save(replyComment);

        // Assert
        assertNotNull(saved.getCommentId());
        assertEquals("This is a reply", saved.getMessage());
        assertEquals("Previous notification", saved.getReplyTo());
        assertEquals(CommentType.NOTIFICATION, saved.getCommentType());
    }

    @Test
    void testUpdateComment_Success() {
        // Arrange
        Comment comment = commentRepository.findById(comment1.getCommentId()).orElseThrow();
        String originalMessage = comment.getMessage();

        // Act
        comment.setMessage("Updated comment message");
        commentRepository.save(comment);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Comment updated = commentRepository.findById(comment1.getCommentId()).orElseThrow();
        assertEquals("Updated comment message", updated.getMessage());
        assertNotEquals(originalMessage, updated.getMessage());
    }

    @Test
    void testDeleteComment_Success() {
        // Arrange
        Long commentId = comment1.getCommentId();

        // Verify comment exists
        assertTrue(commentRepository.findById(commentId).isPresent());

        // Verify context has 2 comments
        List<Comment> beforeDelete = commentRepository.findByContextTypeAndContextId(
            ContextType.OFFER, 100L);
        assertEquals(2, beforeDelete.size());

        // Act
        commentRepository.deleteById(commentId);
        entityManager.flush();

        // Assert
        assertFalse(commentRepository.findById(commentId).isPresent());

        // Verify context now has 1 comment
        List<Comment> afterDelete = commentRepository.findByContextTypeAndContextId(
            ContextType.OFFER, 100L);
        assertEquals(1, afterDelete.size());
        assertEquals("Customer requested additional details", afterDelete.get(0).getMessage());
    }

    @Test
    void testFindAll_ReturnsAllComments() {
        // Act
        List<Comment> allComments = commentRepository.findAll();

        // Assert
        assertNotNull(allComments);
        assertEquals(4, allComments.size());
    }

    @Test
    void testSaveMultipleCommentsOnSameContext_Success() {
        // Arrange
        Comment comment5 = new Comment();
        comment5.setMessage("Third comment on offer");
        comment5.setDate(LocalDateTime.now());
        comment5.setContextType(ContextType.OFFER);
        comment5.setContextId(100L);
        comment5.setCommentType(CommentType.DIRECT);

        Comment comment6 = new Comment();
        comment6.setMessage("Fourth comment on offer");
        comment6.setDate(LocalDateTime.now());
        comment6.setContextType(ContextType.OFFER);
        comment6.setContextId(100L);
        comment6.setCommentType(CommentType.DIRECT);

        // Act
        commentRepository.save(comment5);
        commentRepository.save(comment6);

        // Assert
        List<Comment> offerComments = commentRepository.findByContextTypeAndContextId(
            ContextType.OFFER, 100L);
        assertEquals(4, offerComments.size());
    }

    @Test
    void testCommentDate_PreservesTimestamp() {
        // Arrange
        LocalDateTime specificDate = LocalDateTime.of(2025, 1, 15, 10, 30, 45);
        Comment timedComment = new Comment();
        timedComment.setMessage("Comment with specific timestamp");
        timedComment.setDate(specificDate);
        timedComment.setContextType(ContextType.OFFER);
        timedComment.setContextId(400L);
        timedComment.setCommentType(CommentType.DIRECT);

        // Act
        Comment saved = commentRepository.save(timedComment);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Comment found = commentRepository.findById(saved.getCommentId()).orElseThrow();
        assertEquals(specificDate, found.getDate());
    }
}
