package com.example.sulsul.review.repository;

import com.example.sulsul.common.type.*;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.repository.EssayRepository;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private EssayRepository essayRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void 리뷰생성_테스트() {
        // given
        User s1 = User.builder()
                .name("김경근")
                .email("sulsul@gmail.com")
                .uType(UType.STUDENT)
                .eType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .build();

        User t1 = User.builder()
                .name("임탁균")
                .email("sulsul@naver.com")
                .uType(UType.TEACHER)
                .eType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .catchPhrase("항상 최선을 다하겠습니다. 화이링")
                .build();

        userRepository.save(s1);
        userRepository.save(t1);

        Essay essay1 = Essay.builder()
                .univ("홍익대")
                .examYear("2022")
                .eType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayState(EssayState.COMPLETE)
                .reviewState(ReviewState.OFF)
                .student(s1)
                .teacher(t1)
                .build();

        essayRepository.save(essay1);
        //when
        Review review = Review.builder()
                .detail("구체적으로 첨삭해주셔서 좋았어요.")
                .score(5)
                .essay(essay1)
                .student(s1)
                .teacher(t1)
                .build();

        Review saved = reviewRepository.save(review);
        //then
        assertAll(
                () -> assertThat(saved.getDetail()).isEqualTo("구체적으로 첨삭해주셔서 좋았어요."),
                () -> assertThat(saved.getScore()).isEqualTo(5),
                () -> assertThat(saved.getEssay().getUniv()).isEqualTo("홍익대"),
                () -> assertThat(saved.getEssay().getExamYear()).isEqualTo("2022"),
                () -> assertThat(saved.getEssay().getEType()).isEqualTo("수리"),
                () -> assertThat(saved.getStudent().getName()).isEqualTo("김경근"),
                () -> assertThat(saved.getStudent().getEmail()).isEqualTo("sulsul@gmail.com"),
                () -> assertThat(saved.getTeacher().getName()).isEqualTo("임탁균"),
                () -> assertThat(saved.getTeacher().getEmail()).isEqualTo("sulsul@naver.com")
        );
    }

    @Test
    void 리뷰조회_테스트() {
        // given
        User s1 = User.builder()
                .name("김경근")
                .email("sulsul@gmail.com")
                .uType(UType.STUDENT)
                .eType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .build();

        User t1 = User.builder()
                .name("임탁균")
                .email("sulsul@naver.com")
                .uType(UType.TEACHER)
                .eType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .catchPhrase("항상 최선을 다하겠습니다. 화이링")
                .build();

        userRepository.save(s1);
        userRepository.save(t1);

        Essay essay1 = Essay.builder()
                .univ("홍익대")
                .examYear("2022")
                .eType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayState(EssayState.COMPLETE)
                .reviewState(ReviewState.OFF)
                .student(s1)
                .teacher(t1)
                .build();

        essayRepository.save(essay1);
        //when
        Review review = Review.builder()
                .detail("구체적으로 첨삭해주셔서 좋았어요.")
                .score(5)
                .essay(essay1)
                .student(s1)
                .teacher(t1)
                .build();

        Review saved = reviewRepository.save(review);
        //then
        boolean exists = reviewRepository.findByEssayId(saved.getEssay().getId()).isPresent();
        assertThat(exists).isTrue();
    }

    @Test
    void 강사프로필_리뷰조회_테스트() {
        // given
        User s1 = User.builder()
                .name("김경근")
                .email("sulsul@gmail.com")
                .uType(UType.STUDENT)
                .eType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .build();

        User s2 = User.builder()
                .name("류동완")
                .email("sulsul@g.hongik.ac.kr")
                .uType(UType.STUDENT)
                .eType(EType.SOCIETY)
                .loginType(LoginType.APPLE)
                .build();

        User t1 = User.builder()
                .name("임탁균")
                .email("sulsul@naver.com")
                .uType(UType.TEACHER)
                .eType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .catchPhrase("항상 최선을 다하겠습니다. 화이링")
                .build();

        userRepository.save(s1);
        userRepository.save(s2);
        userRepository.save(t1);

        Essay essay1 = Essay.builder()
                .univ("홍익대")
                .examYear("2022")
                .eType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayState(EssayState.COMPLETE)
                .reviewState(ReviewState.OFF)
                .student(s1)
                .teacher(t1)
                .build();

        essayRepository.save(essay1);
        //when
        Review r1 = Review.builder()
                .detail("구체적으로 첨삭해주셔서 좋았어요.")
                .score(5)
                .essay(essay1)
                .student(s1)
                .teacher(t1)
                .build();

        Review r2 = Review.builder()
                .detail("바쁘셔서 그런지 소통이 잘 안되는 느낌이었어요.")
                .score(3)
                .essay(essay1)
                .student(s2)
                .teacher(t1)
                .build();

        Review saved = reviewRepository.save(r1);
        Long teacherId = saved.getTeacher().getId();
        reviewRepository.save(r2);

        List<Review> reviews = reviewRepository.findAllByTeacherId(teacherId);
        //then
        assertAll(
                () -> assertThat(reviews.size()).isEqualTo(2),
                () -> assertThat(reviews.get(0).getDetail()).isEqualTo("구체적으로 첨삭해주셔서 좋았어요."),
                () -> assertThat(reviews.get(0).getScore()).isEqualTo(5),
                () -> assertThat(reviews.get(1).getDetail()).isEqualTo("바쁘셔서 그런지 소통이 잘 안되는 느낌이었어요."),
                () -> assertThat(reviews.get(1).getScore()).isEqualTo(3)
        );
    }
}