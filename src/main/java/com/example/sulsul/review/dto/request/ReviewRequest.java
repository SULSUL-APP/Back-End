package com.example.sulsul.review.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Getter
@RequiredArgsConstructor
public class ReviewRequest {
    @NotBlank
    @Size(min = 2, max = 100, message = "리뷰는 2글자 이상 100글자 이하입니다.")
    private final String detail;

    @NotBlank
    @PositiveOrZero
    @Max(value = 5, message = "리뷰 점수는 5점 이하의 정수입니다.")
    private final Integer score;
}