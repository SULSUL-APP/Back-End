package com.example.sulsul.handler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class ErrorResponse {

    @Schema(description = "에러 코드")
    private final String code;

    @Schema(description = "에러 메세지")
    private final String message;

    @Schema(description = "에러 세부내용")
    private final Map<String, String> errors;

    @Builder
    public ErrorResponse(String code, String message, Map<String, String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }
}