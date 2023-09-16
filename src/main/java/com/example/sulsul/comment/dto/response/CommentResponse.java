package com.example.sulsul.comment.dto.response;

import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.user.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "단일 댓글 데이터")
public class CommentResponse {

    @Schema(description = "댓글 아이디", example = "1")
    private final Long id;

    @Schema(description = "댓글 내용", example = "댓글 내용 예시")
    private final String detail;

    @Schema(description = "댓글 작성자")
    private final LoginResponse writer;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.detail = comment.getDetail();
        this.writer = new LoginResponse(comment.getUser());
    }
}