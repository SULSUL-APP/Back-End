package com.example.sulsul.essay.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class RejectRequest {
    @Schema(description = "거절사유", example = "거절사유 예시")
    @NotBlank
    @Size(min = 2, max = 20, message = "거절사유는 2글자 이상 20글자 이하입니다.")
    private final String rejectDetail;
}