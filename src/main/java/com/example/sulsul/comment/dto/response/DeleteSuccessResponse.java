package com.example.sulsul.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class DeleteSuccessResponse {

    @Schema(description = "삭제 확인 메시지", example = "댓글 삭제 성공")
    String message;

    public DeleteSuccessResponse(String message) {
        this.message = message;
    }
}