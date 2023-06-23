package com.example.sulsul.review.dto.response;

import com.example.sulsul.review.entity.Review;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ReviewGroupResponse {

    private final List<ReviewResponse> reviews = new ArrayList<>();

    public ReviewGroupResponse(List<Review> reviews) {
        reviews.stream()
                .map(ReviewResponse::new)
                .forEach(review -> this.reviews.add(review));
    }
}