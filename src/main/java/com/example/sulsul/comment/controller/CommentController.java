package com.example.sulsul.comment.controller;

import com.example.sulsul.comment.dto.request.CommentRequest;
import com.example.sulsul.comment.dto.response.CommentGroupResponse;
import com.example.sulsul.comment.dto.response.CommentResponse;
import com.example.sulsul.comment.dto.response.DeleteSuccessResponse;
import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.comment.service.CommentService;
import com.example.sulsul.exception.custom.CustomException;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 해당 첨삭의 모든 댓글 조회
     */
    @GetMapping("/essay/{essayId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long essayId) {
        List<Comment> comments = commentService.getComments(essayId);
        return new ResponseEntity<>(new CommentGroupResponse(comments), HttpStatus.OK);
    }

    /**
     * 해당 첨삭에 댓글 작성
     */
    @PostMapping("/essay/{essayId}/comments")
    public ResponseEntity<?> createComment(@PathVariable Long essayId,
                                           @Valid @RequestBody CommentRequest commentRequest,
                                           BindingResult bindingResult) {
        // 댓글 내용 유효성 검사
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError("detail");
            String message = fieldError.getDefaultMessage();
            throw new CustomException(message);
        }

        // 로그인 되어 있는 유저 객체를 가져오는 로직
        Long userId = 1L; // 임시로 생성한 유저 id;
        User loginedUser = User.builder()
                .id(userId)
                .build();

        Comment comment = commentService.createComment(essayId, loginedUser, commentRequest);
        return new ResponseEntity<>(new CommentResponse(comment), HttpStatus.CREATED);
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId,
                                           @Valid @RequestBody CommentRequest commentRequest,
                                           BindingResult bindingResult) {
        // 댓글 내용 유효성 검사
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError("detail");
            String message = fieldError.getDefaultMessage();
            throw new CustomException(message);
        }

        Comment comment = commentService.updateComment(commentId, commentRequest);
        return new ResponseEntity<>(new CommentResponse(comment), HttpStatus.OK);
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        String message = "댓글 삭제 성공";
        return new ResponseEntity<>(new DeleteSuccessResponse(message), HttpStatus.OK);
    }
}