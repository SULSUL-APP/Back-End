package com.example.sulsul.exceptionhandler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class ErrorResponse {
    @Schema(description = "에러 코드", example = "COMMENT_02")
    private final String code;

    @Schema(description = "에러 메세지", example = "댓글 생성에 실패했습니다.")
    private final String message;

    @Schema(description = "에러 세부내용", example = "{\"detail\":\"댓글은 2글자 이상 100글자 이하입니다.\"}")
    private final Map<String, String> errors;

    @Builder
    public ErrorResponse(String code, String message, Map<String, String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }
}