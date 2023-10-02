package com.example.sulsul.fcm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class FcmTokenSettingSuccess {

    @Schema(description = "FcmToken setting 결과 메세지", example = "FcmToken setting 성공")
    private final String message;

    public FcmTokenSettingSuccess() {
        this.message = "FcmToken setting 성공";
    }
}