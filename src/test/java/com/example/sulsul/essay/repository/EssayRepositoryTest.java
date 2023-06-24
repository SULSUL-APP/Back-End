package com.example.sulsul.essay.repository;

import com.example.sulsul.common.type.EType;
import com.example.sulsul.common.type.LoginType;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.entity.type.EssayState;
import com.example.sulsul.essay.entity.type.ReviewState;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class EssayRepositoryTest {

    @Autowired
    private EssayRepository essayRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void 데이터_준비하기() {
        // 강사, 학생 데이터 생성
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
                .catchPhrase("항상 최선을 다하겠습니다.")
                .build();

        userRepository.save(s1);
        userRepository.save(s2);
        userRepository.save(t1);
    }

    @Test
    void 유저_데이터_확인하기() {
        // given && when
        User s1 = userRepository.findByEmail("sulsul@gmail.com").get();
        User s2 = userRepository.findByEmail("sulsul@g.hongik.ac.kr").get();
        User t1 = userRepository.findByEmail("sulsul@naver.com").get();
        // then
        assertThat(s1.getName()).isEqualTo("김경근");
        assertThat(s2.getName()).isEqualTo("류동완");
        assertThat(t1.getName()).isEqualTo("임탁균");
    }

    @Test
    void 첨삭생성_테스트() {
        // given
        User s1 = userRepository.findByEmail("sulsul@gmail.com").get();
        User t1 = userRepository.findByEmail("sulsul@naver.com").get();

        Essay essay = Essay.builder()
                .univ("홍익대")
                .examYear("2022")
                .eType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayState(EssayState.REQUEST)
                .reviewState(ReviewState.OFF)
                .student(s1)
                .teacher(t1)
                .build();
        // when
        Essay foundEssay = essayRepository.save(essay);
        // then
        assertAll(
                () -> assertThat(foundEssay.getUniv()).isEqualTo("홍익대"),
                () -> assertThat(foundEssay.getExamYear()).isEqualTo("2022"),
                () -> assertThat(foundEssay.getEType()).isEqualTo("수리"),
                () -> assertThat(foundEssay.getEssayState()).isEqualTo(EssayState.REQUEST),
                () -> assertThat(foundEssay.getReviewState()).isEqualTo(ReviewState.OFF),
                () -> assertThat(foundEssay.getStudent()).isEqualTo(s1),
                () -> assertThat(foundEssay.getTeacher()).isEqualTo(t1)
        );
    }
}