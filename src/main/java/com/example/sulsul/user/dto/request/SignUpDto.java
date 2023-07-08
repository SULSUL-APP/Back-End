package com.example.sulsul.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@AllArgsConstructor
public class SignUpDto {

    @Schema(description = "유저의 가입 타입", example = "STUDENT")
    @NotBlank(message = "유저의 가입 타입은 필수 값 입니다.")
    private String uType;

    @Schema(description = "유저의 논술 분야", example = "NATURE")
    @NotBlank(message = "유저의 논술 분야는 필수 값 입니다.")
    private String eType;

}
