package com.example.sulsul.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class SignUpRequest {

    @Schema(description = "유저의 가입 타입", example = "STUDENT")
    @NotBlank(message = "유저의 가입 타입은 필수 값 입니다.")
    private final String userType;

    @Schema(description = "유저의 논술 분야", example = "NATURE")
    @NotBlank(message = "유저의 논술 분야는 필수 값 입니다.")
    private final String essayType;

}
