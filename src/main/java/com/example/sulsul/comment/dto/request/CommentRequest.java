package com.example.sulsul.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class CommentRequest {
    @Schema(description = "댓글 내용", example = "댓글 내용 예시")
    @NotBlank
    @Size(min = 2, max = 100, message = "댓글은 2글자 이상 100글자 이하입니다.")
    private final String detail;
}