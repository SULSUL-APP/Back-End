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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class EssayRepositoryTest {

    @Autowired
    private EssayRepository essayRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @DisplayName("데이터 준비하기")
    void setUp() {
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
                .catchPhrase("항상 최선을 다하겠습니다. 화이링")
                .build();

        User t2 = User.builder()
                .name("전용수")
                .email("smc@gmail.com")
                .uType(UType.TEACHER)
                .eType(EType.SOCIETY)
                .loginType(LoginType.KAKAO)
                .catchPhrase("항상 최선을 다하겠습니다.")
                .build();

        userRepository.save(s1);
        userRepository.save(s2);
        userRepository.save(t1);
        userRepository.save(t2);
    }

    @Test
    @DisplayName("준비한 유저 데이터 확인하기")
    void findUserByEmailTest() {
        // given && when
        User s1 = userRepository.findByEmail("sulsul@gmail.com").get();
        User s2 = userRepository.findByEmail("sulsul@g.hongik.ac.kr").get();
        User t1 = userRepository.findByEmail("sulsul@naver.com").get();
        User t2 = userRepository.findByEmail("smc@gmail.com").get();
        // then
        assertAll(
                () -> assertThat(s1.getName()).isEqualTo("김경근"),
                () -> assertThat(s2.getName()).isEqualTo("류동완"),
                () -> assertThat(t1.getName()).isEqualTo("임탁균"),
                () -> assertThat(t2.getName()).isEqualTo("전용수")
        );
    }

    @Test
    @DisplayName("첨삭 생성 테스트")
    void saveEssayTest() {
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

    @Test
    @DisplayName("첨삭 개별 조회 테스트")
    void findEssayByIdTest() {
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
        Long essayId = essayRepository.save(essay).getId();
        Essay foundEssay = essayRepository.findById(essayId).get();
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

    @Test
    @DisplayName("강사에게 요청된 첨삭목록 조회 테스트")
    void findByTeacherAndEssayStateTest() {
        // given
        User s1 = userRepository.findByEmail("sulsul@gmail.com").get();
        User s2 = userRepository.findByEmail("sulsul@g.hongik.ac.kr").get();
        User t1 = userRepository.findByEmail("sulsul@naver.com").get();

        Essay essay1 = Essay.builder()
                .univ("홍익대")
                .examYear("2022")
                .eType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayState(EssayState.REQUEST)
                .reviewState(ReviewState.OFF)
                .student(s1)
                .teacher(t1)
                .build();

        Essay essay2 = Essay.builder()
                .univ("홍익대")
                .examYear("2023")
                .eType("인문사회")
                .inquiry("2023년 인문 첨삭 부탁드립니다.")
                .essayState(EssayState.REQUEST)
                .reviewState(ReviewState.OFF)
                .student(s2)
                .teacher(t1)
                .build();

        essayRepository.save(essay1);
        essayRepository.save(essay2);
        // when
        List<Essay> essays = essayRepository.findAllByTeacherIdAndEssayState(t1.getId(), EssayState.REQUEST);
        // then
        assertAll(
                () -> assertThat(essays.size()).isEqualTo(2),
                () -> assertThat(essays.get(0).getStudent()).isEqualTo(s1),
                () -> assertThat(essays.get(1).getStudent()).isEqualTo(s2)
        );
    }

    @Test
    @DisplayName("학생이 요청한 첨삭목록 조회 테스트")
    void findByStudentAndEssayStateTest() {
        // given
        User s1 = userRepository.findByEmail("sulsul@gmail.com").get();
        User t1 = userRepository.findByEmail("sulsul@naver.com").get();
        User t2 = userRepository.findByEmail("smc@gmail.com").get();

        Essay essay1 = Essay.builder()
                .univ("홍익대")
                .examYear("2022")
                .eType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayState(EssayState.REQUEST)
                .reviewState(ReviewState.OFF)
                .student(s1)
                .teacher(t1)
                .build();

        Essay essay2 = Essay.builder()
                .univ("홍익대")
                .examYear("2023")
                .eType("인문사회")
                .inquiry("2023년 인문 첨삭 부탁드립니다.")
                .essayState(EssayState.REQUEST)
                .reviewState(ReviewState.OFF)
                .student(s1)
                .teacher(t2)
                .build();

        essayRepository.save(essay1);
        essayRepository.save(essay2);
        // when
        List<Essay> essays = essayRepository.findAllByStudentIdAndEssayState(s1.getId(), EssayState.REQUEST);
        // then
        assertAll(
                () -> assertThat(essays.size()).isEqualTo(2),
                () -> assertThat(essays.get(0).getTeacher()).isEqualTo(t1),
                () -> assertThat(essays.get(1).getTeacher()).isEqualTo(t2)
        );
    }

    @Test
    @DisplayName("첨삭상태 변경 테스트")
    void updateEssayStateTest() {
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
        Essay acceptedEssay = essayRepository.save(essay);
        acceptedEssay.updateEssayState(EssayState.PROCEED);
        essayRepository.save(acceptedEssay);

        Essay foundEssay = essayRepository.findById(acceptedEssay.getId()).get();
        // then
        assertAll(
                () -> assertThat(foundEssay.getUniv()).isEqualTo("홍익대"),
                () -> assertThat(foundEssay.getExamYear()).isEqualTo("2022"),
                () -> assertThat(foundEssay.getEType()).isEqualTo("수리"),
                () -> assertThat(foundEssay.getEssayState()).isEqualTo(EssayState.PROCEED),
                () -> assertThat(foundEssay.getReviewState()).isEqualTo(ReviewState.OFF),
                () -> assertThat(foundEssay.getStudent()).isEqualTo(s1),
                () -> assertThat(foundEssay.getTeacher()).isEqualTo(t1)
        );
    }
}