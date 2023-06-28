package com.example.sulsul.review.service;

import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.common.type.ReviewState;
import com.example.sulsul.exception.CustomException;
import com.example.sulsul.essay.repository.EssayRepository;
import com.example.sulsul.review.dto.request.ReviewRequest;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.review.repository.ReviewRepository;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final EssayRepository essayRepository;
    private final ReviewRepository reviewRepository;

    public Review createReview(Long essayId, User student, ReviewRequest request) {
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new CustomException("해당 첨삭이 존재하지 않습니다."));

        // 리뷰 생성
        Review review = Review.builder()
                .essay(essay)
                .student(student)
                .detail(request.getDetail())
                .score(request.getScore())
                .build();

        // 첨삭리뷰 여부 update
        essay.updateReviewState(ReviewState.ON);

        essayRepository.save(essay);
        return reviewRepository.save(review);
    }

    public List<Review> getReviews(Long teacherId) {
        return reviewRepository.findAllByTeacherId(teacherId);
    }
}