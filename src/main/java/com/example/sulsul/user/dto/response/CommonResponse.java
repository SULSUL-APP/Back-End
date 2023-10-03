package com.example.sulsul.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CommonResponse {

    @Schema(description = "탈퇴 결과 메시지", example = "탈퇴 성공")
    private final String message;

    public CommonResponse() {
        this.message = "회원탈퇴 성공";
    }

}