package com.example.sulsul.review.dto;

import com.example.sulsul.review.entity.Review;
import lombok.Getter;

@Getter
public class ReviewResponse {

    private final Long reviewId;
    private final String detail;
    public final Integer score;

    public ReviewResponse(Review review) {
        this.reviewId = review.getId();
        this.detail = review.getDetail();
        this.score = review.getScore();
    }
}