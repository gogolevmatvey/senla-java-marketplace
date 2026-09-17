package org.example.controller;

import org.example.dto.CommentDto;
import org.example.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable("id") Long id) {
        CommentDto comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    @PostMapping("ads/{adsId}/comment")
    public ResponseEntity<?> addComment(@PathVariable("adsId") Long adsId, @RequestBody CommentDto commentDto) {
        CommentDto createdComment = commentService.addComment(adsId, commentDto);
        return ResponseEntity.ok(createdComment);
    }

    @PatchMapping("/ads/{adsId}/comments/{commentId}")
    public ResponseEntity<?> editComment(@PathVariable("commentId") Long commentId, @RequestBody CommentDto commentDto) {
        CommentDto updatedComment = commentService.editComment(commentId, commentDto);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/ads/{adsId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        //return ResponseEntity.ok(new MessageResponse("Comment successfully deleted"));
        return ResponseEntity.noContent().build();
    }
}
