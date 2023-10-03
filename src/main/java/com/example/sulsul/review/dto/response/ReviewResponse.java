package com.example.sulsul.review.dto.response;

import com.example.sulsul.review.entity.Review;
import com.example.sulsul.user.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ReviewResponse {

    @Schema(description = "리뷰 아이디", example = "1")
    private final Long id;

    @Schema(description = "리뷰 내용", example = "리뷰 내용 예시")
    private final String detail;

    @Schema(description = "리뷰 점수", example = "5")
    public final Integer score;

    @Schema(description = "리뷰 작성자")
    private final LoginResponse writer;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.detail = review.getDetail();
        this.score = review.getScore();
        this.writer = new LoginResponse(review.getStudent());
    }
}