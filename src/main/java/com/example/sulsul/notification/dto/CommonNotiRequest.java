package com.example.sulsul.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class CommonNotiRequest {
    @NotBlank
    @Schema(description = "전체알림 제목", example = "공지사항")
    private final String title;

    @NotBlank
    @Schema(description = "전체알림 내용", example = "서버점검이 예정되어 있습니다.")
    private final String body;
}