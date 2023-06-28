package com.example.sulsul.review.dto.response;

import com.example.sulsul.review.entity.Review;
import com.example.sulsul.user.dto.UserResponse;
import com.example.sulsul.user.entity.User;
import lombok.Getter;

@Getter
public class ReviewResponse {

    private final Long reviewId;
    private final String detail;
    public final Integer score;
    private final UserResponse writer;

    public ReviewResponse(Review review) {
        this.reviewId = review.getId();
        this.detail = review.getDetail();
        this.score = review.getScore();
        this.writer = new UserResponse(review.getStudent());
    }
}