package com.example.sulsul.review.dto;

import com.example.sulsul.review.entity.Review;
import lombok.Getter;

@Getter
public class ReviewResponse {

    private Long reviewId;
    private String detail;
    public Integer score;

    public ReviewResponse(Review review) {
        this.reviewId = review.getId();
        this.detail = review.getDetail();
        this.score = review.getScore();
    }
}