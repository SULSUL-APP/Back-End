package com.example.sulsul.user.dto.request;

import com.example.sulsul.common.type.UType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class PutMyPageRequest {

    @Schema(description = "유저의 논술 분야", example = "NATURE")
    @NotBlank(message = "유저의 논술 분야는 필수 값 입니다.")
    private final String essayType;

    @Schema(description = "유저의 이메일", example = "gyun1712@gmail.com")
    @NotBlank(message = "유저의 이메일은 필수 값 입니다.")
    private final String email;

    @Schema(description = "강사의 프로필 메세지", example = "논술 1타 강사")
    private final String catchPhrase;
}
