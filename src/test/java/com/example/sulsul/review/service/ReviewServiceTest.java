package com.example.sulsul.review.service;

import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.common.type.ReviewState;
import com.example.sulsul.essay.DemoDataFactory;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.repository.EssayRepository;
import com.example.sulsul.review.dto.request.ReviewRequest;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.review.repository.ReviewRepository;
import com.example.sulsul.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private EssayRepository essayRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("리뷰 생성 테스트")
    void createReview() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.COMPLETE, ReviewState.OFF);
        // stub
        when(essayRepository.findById(eq(1L))).thenReturn(Optional.of(essay1));
        when(essayRepository.save(any(Essay.class))).then(returnsFirstArg());
        when(reviewRepository.save(any(Review.class))).then(returnsFirstArg());
        // when
        ReviewRequest request = new ReviewRequest("리뷰 작성 테스트", 1);
        Review review = reviewService.createReview(1L, s1, request);
        // then
        assertAll(
                () -> assertThat(review.getScore()).isEqualTo(1),
                () -> assertThat(review.getDetail()).isEqualTo("리뷰 작성 테스트"),
                () -> assertThat(review.getEssay().getUniv()).isEqualTo("홍익대"),
                () -> assertThat(review.getEssay().getExamYear()).isEqualTo("2022"),
                () -> assertThat(review.getEssay().getEType()).isEqualTo("수리"),
                () -> assertThat(review.getEssay().getReviewState()).isEqualTo(ReviewState.ON),
                () -> assertThat(review.getStudent().getName()).isEqualTo("김경근"),
                () -> assertThat(review.getStudent().getEmail()).isEqualTo("sulsul@gmail.com"),
                () -> assertThat(review.getTeacher().getName()).isEqualTo("임탁균"),
                () -> assertThat(review.getTeacher().getEmail()).isEqualTo("sulsul@naver.com")
        );
    }

    @Test
    @DisplayName("리뷰 조회 테스트")
    void getReviews() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User s2 = DemoDataFactory.createStudent2(2L);
        User t1 = DemoDataFactory.createTeacher1(3L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.COMPLETE, ReviewState.ON);
        Essay essay2 = DemoDataFactory.createEssay1(2L, s2, t1, EssayState.COMPLETE, ReviewState.ON);
        // stub
        Review review1 = DemoDataFactory.createReview1(1L, essay1, s1, t1);
        Review review2 = DemoDataFactory.createReview2(2L, essay2, s2, t1);
        when(reviewRepository.findAllByTeacherId(eq(2L))).thenReturn(List.of(review1, review2));
        // when
        List<Review> reviews = reviewService.getReviews(2L);
        // then
        assertAll(
                () -> assertThat(reviews.size()).isEqualTo(2),
                () -> assertThat(reviews.get(0).getScore()).isEqualTo(5),
                () -> assertThat(reviews.get(0).getDetail()).isEqualTo("구체적으로 첨삭해주셔서 좋았어요."),
                () -> assertThat(reviews.get(1).getScore()).isEqualTo(2),
                () -> assertThat(reviews.get(1).getDetail()).isEqualTo("바쁘셔서 그런지 의사소통이 잘 안되는 것 같았어요.")
        );
    }
}