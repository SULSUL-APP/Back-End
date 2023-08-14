package com.example.sulsul.notification.dto;

import lombok.Getter;

@Getter
public class CommonNotiResponse {

    private final String message;

    public CommonNotiResponse() {
        this.message = "전체알림 전송 성공";
    }
}