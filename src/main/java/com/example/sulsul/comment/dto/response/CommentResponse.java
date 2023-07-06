package com.example.sulsul.comment.dto.response;

import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CommentResponse {

    @Schema(description = "댓글 아이디", example = "1")
    private final Long id;

    @Schema(description = "댓글 내용", example = "댓글 내용 예시")
    private final String detail;

    @Schema(description = "댓글 작성자")
    private final UserResponse writer;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.detail = comment.getDetail();
        this.writer = new UserResponse(comment.getUser());
    }
}