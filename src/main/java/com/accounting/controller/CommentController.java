package com.accounting.controller;

import com.accounting.dto.CommentDTO;
import com.accounting.entity.Comment;
import com.accounting.exeption.ErrorResponse;
import com.accounting.service.comment.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/comment")
@AllArgsConstructor
public class CommentController {

    @Autowired
    private final CommentService commentService;

    @GetMapping("/{contextType}/{contextId}")
    public ResponseEntity<?> getComments(@PathVariable String contextType, @PathVariable Long contextId) {
        try {
            List<CommentDTO> comments = commentService.getComments(contextType, contextId);
            return ResponseEntity.ok(comments);
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Comment not found", HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping()
    public ResponseEntity<Comment> saveComment(@RequestBody CommentDTO commentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.addComment(commentDTO));
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<?> editComment(@PathVariable Long id, @RequestBody CommentDTO updateDto) {
//        try {
//            CommentDTO updatedComment = commentService.editComment(id, updateDto);
//            return ResponseEntity.ok(updatedComment);
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body(new ErrorResponse("Comment not found", HttpStatus.NOT_FOUND.value()));
//        } catch (IllegalArgumentException | CommentNotFoundException e) {
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
//        }
//    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
//        try {
//            // Attempt to delete the client by calling the service layer
//            commentService.deleteCommentById(id);
//
//            // Return a 204 No Content status on successful deletion
//            return ResponseEntity.noContent().build();
//
//        } catch (CommentNotFoundException e) {
//            // If the client is not found, return a 404 status with an error message
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body(new ErrorResponse("Comment not found", HttpStatus.NOT_FOUND.value()));
//
//        }
//    }

}
