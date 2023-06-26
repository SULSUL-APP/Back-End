package com.example.sulsul.review.controller;

import com.example.sulsul.exception.CustomValidationException;
import com.example.sulsul.review.dto.response.ReviewGroupResponse;
import com.example.sulsul.review.dto.response.ReviewResponse;
import com.example.sulsul.review.dto.request.ReviewRequest;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.review.service.ReviewService;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 해당 첨삭에 리뷰 작성
     */
    @PostMapping("/essay/{essayId}/reviews")
    public ResponseEntity<?> createReview(@PathVariable Long essayId,
                                          @Valid @RequestBody ReviewRequest reviewRequest,
                                          BindingResult bindingResult) {
        // 리뷰 내용 유효성 검사
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            throw new CustomValidationException("입력값 유효성 검사 실패", errorMap);
        }

        // 로그인 되어 있는 유저 객체를 가져오는 로직
        Long userId = 1L;
        User loginedUser = User.builder()
                .id(userId)
                .build();

        Review review = reviewService.createReview(essayId, loginedUser, reviewRequest);
        return new ResponseEntity<>(new ReviewResponse(review), HttpStatus.CREATED);
    }

    /**
     * 강사에게 작성된 모든 리뷰 조회
     */
    @GetMapping("/profiles/{profileId}/reviews")
    public ResponseEntity<?> getReviews(@PathVariable Long profileId) {
        List<Review> reviews = reviewService.getReviews(profileId);
        return new ResponseEntity<>(new ReviewGroupResponse(reviews), HttpStatus.OK);
    }
}