package com.example.sulsul.essay.repository;

import com.example.sulsul.common.type.EType;
import com.example.sulsul.common.type.LoginType;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.common.type.ReviewState;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@ActiveProfiles("test")
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
                .userType(UType.STUDENT)
                .essayType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .build();

        User s2 = User.builder()
                .name("류동완")
                .email("sulsul@g.hongik.ac.kr")
                .userType(UType.STUDENT)
                .essayType(EType.SOCIETY)
                .loginType(LoginType.APPLE)
                .build();

        User t1 = User.builder()
                .name("임탁균")
                .email("sulsul@naver.com")
                .userType(UType.TEACHER)
                .essayType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .catchPhrase("항상 최선을 다하겠습니다. 화이링")
                .build();

        User t2 = User.builder()
                .name("전용수")
                .email("smc@gmail.com")
                .userType(UType.TEACHER)
                .essayType(EType.SOCIETY)
                .loginType(LoginType.KAKAO)
                .catchPhrase("항상 최선을 다하겠습니다.")
                .build();

        userRepository.save(s1);
        userRepository.save(s2);
        userRepository.save(t1);
        userRepository.save(t2);
    }

    @Test
    void 준비한_유저_데이터_확인하기() {
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
    void 첨삭_생성_테스트() {
        // given
        User s1 = userRepository.findByEmail("sulsul@gmail.com").get();
        User t1 = userRepository.findByEmail("sulsul@naver.com").get();

        Essay essay = Essay.builder()
                .univ("홍익대")
                .examYear("2022")
                .essayType("수리")
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
                () -> assertThat(foundEssay.getEssayType()).isEqualTo("수리"),
                () -> assertThat(foundEssay.getEssayState()).isEqualTo(EssayState.REQUEST),
                () -> assertThat(foundEssay.getReviewState()).isEqualTo(ReviewState.OFF),
                () -> assertThat(foundEssay.getStudent()).isEqualTo(s1),
                () -> assertThat(foundEssay.getTeacher()).isEqualTo(t1)
        );
    }

    @Test
    void 첨삭_개별조회_테스트() {
        // given
        User s1 = userRepository.findByEmail("sulsul@gmail.com").get();
        User t1 = userRepository.findByEmail("sulsul@naver.com").get();

        Essay essay = Essay.builder()
                .univ("홍익대")
                .examYear("2022")
                .essayType("수리")
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
                () -> assertThat(foundEssay.getEssayType()).isEqualTo("수리"),
                () -> assertThat(foundEssay.getEssayState()).isEqualTo(EssayState.REQUEST),
                () -> assertThat(foundEssay.getReviewState()).isEqualTo(ReviewState.OFF),
                () -> assertThat(foundEssay.getStudent()).isEqualTo(s1),
                () -> assertThat(foundEssay.getTeacher()).isEqualTo(t1)
        );
    }

    @Test
    void 강사에게_요청된_첨삭목록_조회_테스트() {
        // given
        User s1 = userRepository.findByEmail("sulsul@gmail.com").get();
        User s2 = userRepository.findByEmail("sulsul@g.hongik.ac.kr").get();
        User t1 = userRepository.findByEmail("sulsul@naver.com").get();

        Essay essay1 = Essay.builder()
                .univ("홍익대")
                .examYear("2022")
                .essayType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayState(EssayState.REQUEST)
                .reviewState(ReviewState.OFF)
                .student(s1)
                .teacher(t1)
                .build();

        Essay essay2 = Essay.builder()
                .univ("홍익대")
                .examYear("2023")
                .essayType("인문사회")
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
    void 학생이_요청한_첨삭목록_조회_테스트() {
        // given
        User s1 = userRepository.findByEmail("sulsul@gmail.com").get();
        User t1 = userRepository.findByEmail("sulsul@naver.com").get();
        User t2 = userRepository.findByEmail("smc@gmail.com").get();

        Essay essay1 = Essay.builder()
                .univ("홍익대")
                .examYear("2022")
                .essayType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayState(EssayState.REQUEST)
                .reviewState(ReviewState.OFF)
                .student(s1)
                .teacher(t1)
                .build();

        Essay essay2 = Essay.builder()
                .univ("홍익대")
                .examYear("2023")
                .essayType("인문사회")
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
    void 첨삭상태_변경_테스트() {
        // given
        User s1 = userRepository.findByEmail("sulsul@gmail.com").get();
        User t1 = userRepository.findByEmail("sulsul@naver.com").get();

        Essay essay = Essay.builder()
                .univ("홍익대")
                .examYear("2022")
                .essayType("수리")
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
                () -> assertThat(foundEssay.getEssayType()).isEqualTo("수리"),
                () -> assertThat(foundEssay.getEssayState()).isEqualTo(EssayState.PROCEED),
                () -> assertThat(foundEssay.getReviewState()).isEqualTo(ReviewState.OFF),
                () -> assertThat(foundEssay.getStudent()).isEqualTo(s1),
                () -> assertThat(foundEssay.getTeacher()).isEqualTo(t1)
        );
    }
}