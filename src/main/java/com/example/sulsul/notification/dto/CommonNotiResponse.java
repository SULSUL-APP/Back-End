package com.example.sulsul.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CommonNotiResponse {

    @Schema(description = "알림전송 결과 메시지", example = "전체알림 전송 성공")
    private final String message;

    public CommonNotiResponse() {
        this.message = "전체알림 전송 성공";
    }
}