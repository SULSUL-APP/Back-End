package com.example.sulsul.review.dto.response;

import com.example.sulsul.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ReviewGroupResponse {

    @Schema(description = "리뷰 리스트")
    private final List<ReviewResponse> reviews = new ArrayList<>();

    public ReviewGroupResponse(List<Review> reviews) {
        reviews.stream()
                .map(ReviewResponse::new)
                .forEach(review -> this.reviews.add(review));
    }
}