package com.example.sulsul.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class SignUpDto {

    @Schema(description = "유저의 가입 타입", example = "STUDENT")
    @NotBlank(message = "유저의 가입 타입은 필수 값 입니다.")
    private final String uType;

    @Schema(description = "유저의 논술 분야", example = "NATURE")
    @NotBlank(message = "유저의 논술 분야는 필수 값 입니다.")
    private final String eType;

}
