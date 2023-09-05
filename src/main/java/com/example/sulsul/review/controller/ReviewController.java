package com.example.sulsul.review.controller;

import com.example.sulsul.config.security.CustomUserDetails;
import com.example.sulsul.exception.review.InvalidReviewCreateException;
import com.example.sulsul.exceptionhandler.ErrorResponse;
import com.example.sulsul.review.dto.request.ReviewRequest;
import com.example.sulsul.review.dto.response.ReviewGroupResponse;
import com.example.sulsul.review.dto.response.ReviewResponse;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.review.service.ReviewService;
import com.example.sulsul.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Review", description = "리뷰 관련 API")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "해당 첨삭에 리뷰 작성", description = "essayId에 해당하는 첨삭에 리뷰를 작성한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/essay/{essayId}/reviews")
    public ResponseEntity<?> createReview(@Parameter(description = "리뷰를 작성할 첨삭의 id")
                                          @PathVariable Long essayId,
                                          @Valid @RequestBody ReviewRequest reviewRequest,
                                          @AuthenticationPrincipal CustomUserDetails loginedUser,
                                          BindingResult bindingResult) {
        // 리뷰 내용 유효성 검사
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            throw new InvalidReviewCreateException(errorMap);
        }

        // 로그인 되어 있는 유저 객체를 가져오는 로직
        User user = loginedUser.getUser();
        Review review = reviewService.createReview(essayId, user, reviewRequest);
        return new ResponseEntity<>(new ReviewResponse(review), HttpStatus.CREATED);
    }

    @Operation(summary = "강사에게 작성된 모든 리뷰 조회", description = "profileId에 해당하는 강사에게 작성된 모든 리뷰를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReviewGroupResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/profiles/{profileId}/reviews")
    public ResponseEntity<?> getReviews(@Parameter(description = "리뷰를 조회할 강사의 id")
                                        @PathVariable Long profileId) {
        List<Review> reviews = reviewService.getReviews(profileId);
        return new ResponseEntity<>(new ReviewGroupResponse(reviews), HttpStatus.OK);
    }
}