package com.example.sulsul.comment.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class CommentRequest {
    @NotBlank
    @Size(min = 2, max = 100, message = "댓글은 2글자 이상 100글자 이하입니다.")
    private final String detail;
}