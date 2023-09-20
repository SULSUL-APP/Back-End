package com.example.sulsul.review.service;

import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.common.type.ReviewState;
import com.example.sulsul.essay.repository.EssayRepository;
import com.example.sulsul.exception.essay.EssayNotFoundException;
import com.example.sulsul.review.dto.request.ReviewRequest;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.review.repository.ReviewRepository;
import com.example.sulsul.teacherprofile.service.TeacherProfileService;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final EssayRepository essayRepository;
    private final ReviewRepository reviewRepository;
    private final TeacherProfileService teacherProfileService;

    public Review createReview(Long essayId, User student, ReviewRequest request) {
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new EssayNotFoundException(essayId));

        // 리뷰 생성
        Review review = Review.builder()
                .essay(essay)
                .student(student)
                .teacher(essay.getTeacher())
                .detail(request.getDetail())
                .score(request.getScore())
                .build();

        // 첨삭리뷰 여부 update
        essay.updateReviewState(ReviewState.ON);
        essayRepository.save(essay);

        // 강사 리뷰 평점, 완료된 첨삭 수 update
        teacherProfileService.regradeTeacherProfile(request.getScore(), essay.getTeacher());

        return reviewRepository.save(review);
    }

    public List<Review> getReviews(Long teacherId) {
        return reviewRepository.findAllByTeacherId(teacherId);
    }
}