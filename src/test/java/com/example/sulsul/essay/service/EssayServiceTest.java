package com.example.sulsul.essay.service;

import com.example.sulsul.comment.repository.CommentRepository;
import com.example.sulsul.common.type.EType;
import com.example.sulsul.common.type.LoginType;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.essay.dto.request.CreateEssayRequest;
import com.example.sulsul.essay.dto.response.EssayResponse;
import com.example.sulsul.essay.dto.response.RejectEssayResponse;
import com.example.sulsul.essay.dto.response.RequestEssayResponse;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.entity.type.EssayState;
import com.example.sulsul.essay.entity.type.ReviewState;
import com.example.sulsul.essay.repository.EssayRepository;
import com.example.sulsul.file.entity.File;
import com.example.sulsul.file.repository.FileRepository;
import com.example.sulsul.review.repository.ReviewRepository;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EssayServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EssayRepository essayRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private EssayService essayService;

    @Test
    @DisplayName("첨삭생성 테스트")
    void createEssayTest() {
        // given
        User s1 = User.builder()
                .name("김경근")
                .email("sulsul@gmail.com")
                .uType(UType.STUDENT)
                .eType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .build();

        Long profileId = 2L;
        User t1 = User.builder()
                .id(profileId)
                .name("임탁균")
                .email("sulsul@naver.com")
                .uType(UType.TEACHER)
                .eType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .catchPhrase("항상 최선을 다하겠습니다. 화이링")
                .build();

        CreateEssayRequest request = CreateEssayRequest.builder()
                .univ("홍익대")
                .examYear("2022")
                .eType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
//                .file() // TODO: 첨삭파일 테스트는 보류, 공부해야 함..
                .build();
        // stub
        when(userRepository.findById(profileId)).thenReturn(Optional.of(t1));
        when(essayRepository.save(any())).then(returnsFirstArg());
        Essay essay = essayService.createEssay(profileId, s1, request);
        // then
        assertAll(
                () -> assertThat(essay.getUniv()).isEqualTo("홍익대"),
                () -> assertThat(essay.getExamYear()).isEqualTo("2022"),
                () -> assertThat(essay.getEType()).isEqualTo("수리"),
                () -> assertThat(essay.getEssayState()).isEqualTo(EssayState.REQUEST),
                () -> assertThat(essay.getReviewState()).isEqualTo(ReviewState.OFF),
                () -> assertThat(essay.getStudent()).isEqualTo(s1),
                () -> assertThat(essay.getTeacher()).isEqualTo(t1)
        );
    }

    @Test
    @DisplayName("강사에게 요청된 첨삭목록 조회 테스트")
    void getEssaysByTeacherTest() {
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

        Long teacherId = 3L;
        User t1 = User.builder()
                .id(teacherId)
                .name("임탁균")
                .email("sulsul@naver.com")
                .uType(UType.TEACHER)
                .eType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .catchPhrase("항상 최선을 다하겠습니다. 화이링")
                .build();

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
        // stub
        when(essayRepository.findAllByTeacherIdAndEssayState(teacherId, EssayState.REQUEST))
                .thenReturn(List.of(essay1, essay2));
        List<Essay> essays = essayService.getEssays(t1, EssayState.REQUEST);
        // then
        assertAll(
                () -> assertThat(essays.size()).isEqualTo(2),
                () -> assertThat(essays.get(0).getStudent()).isEqualTo(s1),
                () -> assertThat(essays.get(1).getStudent()).isEqualTo(s2)
        );
    }

    @Test
    @DisplayName("학생이 요청한 첨삭목록 조회 테스트")
    void getEssaysByStudentTest() {
        // given
        Long studentId = 1L;
        User s1 = User.builder()
                .id(studentId)
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

        User t2 = User.builder()
                .name("전용수")
                .email("smc@gmail.com")
                .uType(UType.TEACHER)
                .eType(EType.SOCIETY)
                .loginType(LoginType.KAKAO)
                .catchPhrase("항상 최선을 다하겠습니다.")
                .build();

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
        // stub
        when(essayRepository.findAllByStudentIdAndEssayState(studentId, EssayState.REQUEST))
                .thenReturn(List.of(essay1, essay2));
        List<Essay> essays = essayService.getEssays(s1, EssayState.REQUEST);
        // then
        assertAll(
                () -> assertThat(essays.size()).isEqualTo(2),
                () -> assertThat(essays.get(0).getTeacher()).isEqualTo(t1),
                () -> assertThat(essays.get(1).getTeacher()).isEqualTo(t2)
        );
    }

    @Test
    @DisplayName("요청상태인 첨삭 개별조회 테스트")
    void getRequestEssayWithStudentFileTest() {
        // given
        User s1 = User.builder()
                .id(1L)
                .name("김경근")
                .email("sulsul@gmail.com")
                .uType(UType.STUDENT)
                .eType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .build();

        User t1 = User.builder()
                .id(2L)
                .name("임탁균")
                .email("sulsul@naver.com")
                .uType(UType.TEACHER)
                .eType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .catchPhrase("항상 최선을 다하겠습니다. 화이링")
                .build();

        Essay essay1 = Essay.builder()
                .id(1L)
                .univ("홍익대")
                .examYear("2022")
                .eType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayState(EssayState.REQUEST) // 첨삭요청 상태
                .reviewState(ReviewState.OFF)
                .student(s1)
                .teacher(t1)
                .build();
        // stub

        String filePath = "http://s3-ap-northeast-2.amazonaws.com/sulsul";
        when(essayRepository.findById(1L)).thenReturn(Optional.of(essay1));
        when(fileRepository.getStudentEssayFile(1L, 1L))
                .thenReturn(Optional.of(File.builder()
                        .id(1L)
                        .filePath(filePath)
                        .build()));
        RequestEssayResponse response = (RequestEssayResponse) essayService.getEssayWithStudentFile(1L);
        // when
        assertAll(
                () -> assertThat(response.getUniv()).isEqualTo("홍익대"),
                () -> assertThat(response.getExamYear()).isEqualTo("2022"),
                () -> assertThat(response.getEType()).isEqualTo("수리"),
                () -> assertThat(response.getInquiry()).isEqualTo("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다."),
                () -> assertThat(response.getEssayState()).isEqualTo(EssayState.REQUEST),
                () -> assertThat(response.getStudentFilePath()).isEqualTo(filePath)
        );
    }

    @Test
    @DisplayName("거절상태인 첨삭 개별조회 테스트")
    void getRejectEssayWithStudentFileTest() {
        // given
        User s1 = User.builder()
                .id(1L)
                .name("김경근")
                .email("sulsul@gmail.com")
                .uType(UType.STUDENT)
                .eType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .build();

        User t1 = User.builder()
                .id(2L)
                .name("임탁균")
                .email("sulsul@naver.com")
                .uType(UType.TEACHER)
                .eType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .catchPhrase("항상 최선을 다하겠습니다. 화이링")
                .build();

        Essay essay1 = Essay.builder()
                .id(1L)
                .univ("홍익대")
                .examYear("2022")
                .eType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayState(EssayState.REJECT) // 첨삭거절 상태
                .reviewState(ReviewState.OFF)
                .student(s1)
                .teacher(t1)
                .build();
        // stub
        String filePath = "http://s3-ap-northeast-2.amazonaws.com/sulsul";
        when(essayRepository.findById(1L)).thenReturn(Optional.of(essay1));
        when(fileRepository.getStudentEssayFile(1L, 1L))
                .thenReturn(Optional.of(File.builder()
                        .id(1L)
                        .filePath(filePath)
                        .build()));
        RejectEssayResponse response = (RejectEssayResponse) essayService.getEssayWithStudentFile(1L);
        // when
        assertAll(
                () -> assertThat(response.getUniv()).isEqualTo("홍익대"),
                () -> assertThat(response.getExamYear()).isEqualTo("2022"),
                () -> assertThat(response.getEType()).isEqualTo("수리"),
                () -> assertThat(response.getInquiry()).isEqualTo("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다."),
                () -> assertThat(response.getEssayState()).isEqualTo(EssayState.REJECT),
                () -> assertThat(response.getStudentFilePath()).isEqualTo(filePath)
        );
    }

    @Test
    void getEssayWithFilePaths() {
    }

    @Test
    void acceptEssay() {
    }

    @Test
    void rejectEssay() {
    }

    @Test
    void completeEssay() {
    }
}