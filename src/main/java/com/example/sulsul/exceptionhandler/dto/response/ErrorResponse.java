package com.example.sulsul.exceptionhandler.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "에러 코드")
    private int errorCode;

    @Schema(description = "에러 메시지")
    private String message;
}