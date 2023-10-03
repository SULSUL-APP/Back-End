package com.example.sulsul.essay.service;

import com.example.sulsul.comment.dto.response.CommentResponse;
import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.comment.repository.CommentRepository;
import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.common.type.ReviewState;
import com.example.sulsul.essay.DemoDataFactory;
import com.example.sulsul.essay.dto.request.CreateEssayRequest;
import com.example.sulsul.essay.dto.request.RejectRequest;
import com.example.sulsul.essay.dto.response.*;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.repository.EssayRepository;
import com.example.sulsul.file.entity.File;
import com.example.sulsul.file.repository.FileRepository;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.review.repository.ReviewRepository;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    void 첨삭생성_테스트() throws IOException {
        // given
        Long profileId = 2L;
        User t1 = DemoDataFactory.createTeacher1(profileId);
        User s1 = DemoDataFactory.createStudent1(1L);
        // 테스트용 첨삭파일 생성
        String fileName = "test";
        String contentType = "pdf";
        String originalFileName = fileName + "." + contentType;
        String filePath = "src/test/resources/pdf/test.pdf";
        FileInputStream fileInputStream = new FileInputStream(filePath);
        MockMultipartFile testFile = new MockMultipartFile(fileName, originalFileName, contentType, fileInputStream);
        // 첨삭요청 생성
        CreateEssayRequest request = CreateEssayRequest.builder()
                .univ("홍익대")
                .examYear("2022")
                .essayType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayFile(testFile)
                .build();
        // stub
        when(userRepository.findById(profileId)).thenReturn(Optional.of(t1));
        when(essayRepository.save(any(Essay.class))).then(returnsFirstArg());
        // when
        Essay essay = essayService.createEssay(profileId, s1, request);
        // then
        assertAll(
                () -> assertThat(essay.getUniv()).isEqualTo("홍익대"),
                () -> assertThat(essay.getExamYear()).isEqualTo("2022"),
                () -> assertThat(essay.getEssayType()).isEqualTo("수리"),
                () -> assertThat(essay.getEssayState()).isEqualTo(EssayState.REQUEST),
                () -> assertThat(essay.getReviewState()).isEqualTo(ReviewState.OFF),
                () -> assertThat(essay.getStudent().getName()).isEqualTo("김경근"),
                () -> assertThat(essay.getStudent().getEmail()).isEqualTo("sulsul@gmail.com"),
                () -> assertThat(essay.getTeacher().getName()).isEqualTo("임탁균"),
                () -> assertThat(essay.getTeacher().getEmail()).isEqualTo("sulsul@naver.com")
        );
    }

    @Test
    void 강사에게_요청된_첨삭목록_조회_테스트() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User s2 = DemoDataFactory.createStudent2(2L);
        long teacherId = 3L;
        User t1 = DemoDataFactory.createTeacher1(teacherId);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.REQUEST, ReviewState.OFF);
        Essay essay2 = DemoDataFactory.createEssay2(2L, s2, t1, EssayState.REQUEST, ReviewState.OFF);
        // stub
        when(essayRepository.findAllByTeacherIdAndEssayState(teacherId, EssayState.REQUEST))
                .thenReturn(List.of(essay1, essay2));
        // when
        List<Essay> essays = essayService.getEssaysByUser(t1, EssayState.REQUEST);
        // then
        assertAll(
                () -> assertThat(essays.size()).isEqualTo(2),
                () -> assertThat(essays.get(0).getStudent().getName()).isEqualTo("김경근"),
                () -> assertThat(essays.get(0).getStudent().getEmail()).isEqualTo("sulsul@gmail.com"),
                () -> assertThat(essays.get(1).getStudent().getName()).isEqualTo("류동완"),
                () -> assertThat(essays.get(1).getStudent().getEmail()).isEqualTo("sulsul@g.hongik.ac.kr"),
                () -> assertThat(essays.get(0).getTeacher().getName()).isEqualTo("임탁균"),
                () -> assertThat(essays.get(0).getTeacher().getEmail()).isEqualTo("sulsul@naver.com"),
                () -> assertThat(essays.get(1).getTeacher().getName()).isEqualTo("임탁균"),
                () -> assertThat(essays.get(1).getTeacher().getEmail()).isEqualTo("sulsul@naver.com")
        );
    }

    @Test
    void 학생이_요청한_첨삭목록_조회_테스트() {
        // given
        long studentId = 1L;
        User s1 = DemoDataFactory.createStudent1(studentId);
        User t1 = DemoDataFactory.createTeacher1(2L);
        User t2 = DemoDataFactory.createTeacher2(3L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.REQUEST, ReviewState.OFF);
        Essay essay2 = DemoDataFactory.createEssay2(2L, s1, t2, EssayState.REQUEST, ReviewState.OFF);
        // stub
        when(essayRepository.findAllByStudentIdAndEssayState(studentId, EssayState.REQUEST))
                .thenReturn(List.of(essay1, essay2));
        // when
        List<Essay> essays = essayService.getEssaysByUser(s1, EssayState.REQUEST);
        // then
        assertAll(
                () -> assertThat(essays.size()).isEqualTo(2),
                () -> assertThat(essays.get(0).getStudent().getName()).isEqualTo("김경근"),
                () -> assertThat(essays.get(0).getStudent().getEmail()).isEqualTo("sulsul@gmail.com"),
                () -> assertThat(essays.get(1).getStudent().getName()).isEqualTo("김경근"),
                () -> assertThat(essays.get(1).getStudent().getEmail()).isEqualTo("sulsul@gmail.com"),
                () -> assertThat(essays.get(0).getTeacher().getName()).isEqualTo("임탁균"),
                () -> assertThat(essays.get(0).getTeacher().getEmail()).isEqualTo("sulsul@naver.com"),
                () -> assertThat(essays.get(1).getTeacher().getName()).isEqualTo("전용수"),
                () -> assertThat(essays.get(1).getTeacher().getEmail()).isEqualTo("smc@gmail.com")
        );
    }

    @Test
    void 요청상태인_첨삭_개별조회_테스트() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.REQUEST, ReviewState.OFF);
        // stub
        when(essayRepository.findById(1L)).thenReturn(Optional.of(essay1));
        String filePath = "http://s3-ap-northeast-2.amazonaws.com/sulsul";
        when(fileRepository.getStudentEssayFile(1L, 1L))
                .thenReturn(Optional.of(File.builder()
                        .id(1L)
                        .filePath(filePath)
                        .build()));
        // when
        RequestEssayResponse response = essayService.getEssayRequest(1L);
        // then
        assertAll(
                () -> assertThat(response.getUniv()).isEqualTo("홍익대"),
                () -> assertThat(response.getExamYear()).isEqualTo("2022"),
                () -> assertThat(response.getEssayType()).isEqualTo("수리"),
                () -> assertThat(response.getInquiry()).isEqualTo("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다."),
                () -> assertThat(response.getEssayState()).isEqualTo("REQUEST"),
                () -> assertThat(response.getStudentFilePath()).isEqualTo(filePath)
        );
    }

    @Test
    void 거절상태인_첨삭_개별조회_테스트() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.REJECT, ReviewState.OFF);
        // stub
        when(essayRepository.findById(1L)).thenReturn(Optional.of(essay1));
        String filePath = "http://s3-ap-northeast-2.amazonaws.com/sulsul/20230624.pdf";
        when(fileRepository.getStudentEssayFile(1L, 1L))
                .thenReturn(Optional.of(File.builder()
                        .id(1L)
                        .filePath(filePath)
                        .build()));

        RejectedEssayResponse response = essayService.getEssayReject(1L);
        // then
        assertAll(
                () -> assertThat(response.getUniv()).isEqualTo("홍익대"),
                () -> assertThat(response.getExamYear()).isEqualTo("2022"),
                () -> assertThat(response.getEssayType()).isEqualTo("수리"),
                () -> assertThat(response.getInquiry()).isEqualTo("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다."),
                () -> assertThat(response.getEssayState()).isEqualTo("REJECT"),
                () -> assertThat(response.getStudentFilePath()).isEqualTo(filePath)
        );
    }

    // TODO: 강사 첨삭파일 유무, 댓글 유무에 따른 테스트 고민
    @Test
    void 진행상태인_첨삭_개별조회_테스트() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.PROCEED, ReviewState.OFF);
        Comment c1 = DemoDataFactory.createComment1(1L, t1, essay1);
        Comment c2 = DemoDataFactory.createComment2(2L, s1, essay1);
        // when
        when(essayRepository.findById(1L)).thenReturn(Optional.of(essay1));
        String filePath1 = "http://s3-ap-northeast-2.amazonaws.com/sulsul/20230624.pdf";
        String filePath2 = "http://s3-ap-northeast-2.amazonaws.com/sulsul/20230625.pdf";
        when(fileRepository.getStudentEssayFile(1L, 1L))
                .thenReturn(Optional.of(File.builder()
                        .id(1L)
                        .filePath(filePath1)
                        .build()));
        when(fileRepository.getTeacherEssayFile(1L, 2L))
                .thenReturn(Optional.of(File.builder()
                        .id(2L)
                        .filePath(filePath2)
                        .build()));
        when(commentRepository.findAllByEssayId(1L)).thenReturn(List.of(c1, c2));

        ProceedEssayResponse response = essayService.getProceedEssay(1L);
        List<CommentResponse> comments = response.getComments();
        // then
        assertAll(
                () -> assertThat(response.getUniv()).isEqualTo("홍익대"),
                () -> assertThat(response.getExamYear()).isEqualTo("2022"),
                () -> assertThat(response.getEssayType()).isEqualTo("수리"),
                () -> assertThat(response.getInquiry()).isEqualTo("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다."),
                () -> assertThat(response.getEssayState()).isEqualTo("PROCEED"),
                () -> assertThat(response.getStudentFilePath()).isEqualTo(filePath1),
                () -> assertThat(response.getTeacherFilePath()).isEqualTo(filePath2),
                () -> assertThat(comments.size()).isEqualTo(2),
                () -> assertThat(comments.get(0).getDetail()).isEqualTo("첨삭한 파일 첨부했습니다."),
                () -> assertThat(comments.get(1).getDetail()).isEqualTo("네 확인했습니다.")
        );
    }

    @Test
    void 리뷰가_없는_완료상태의_첨삭_개별조회_테스트() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.COMPLETE, ReviewState.OFF);
        Comment c1 = DemoDataFactory.createComment1(1L, t1, essay1);
        Comment c2 = DemoDataFactory.createComment2(2L, s1, essay1);
        // when
        when(essayRepository.findById(1L)).thenReturn(Optional.of(essay1));
        String filePath1 = "http://s3-ap-northeast-2.amazonaws.com/sulsul/20230624.pdf";
        String filePath2 = "http://s3-ap-northeast-2.amazonaws.com/sulsul/20230625.pdf";
        when(fileRepository.getStudentEssayFile(1L, 1L))
                .thenReturn(Optional.of(File.builder()
                        .id(1L)
                        .filePath(filePath1)
                        .build()));
        when(fileRepository.getTeacherEssayFile(1L, 2L))
                .thenReturn(Optional.of(File.builder()
                        .id(2L)
                        .filePath(filePath2)
                        .build()));
        when(commentRepository.findAllByEssayId(1L)).thenReturn(List.of(c1, c2));

        CompletedEssayResponse response = essayService.getCompleteEssay(1L);
        List<CommentResponse> comments = response.getComments();
        // then
        assertAll(
                () -> assertThat(response.getUniv()).isEqualTo("홍익대"),
                () -> assertThat(response.getExamYear()).isEqualTo("2022"),
                () -> assertThat(response.getEssayType()).isEqualTo("수리"),
                () -> assertThat(response.getInquiry()).isEqualTo("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다."),
                () -> assertThat(response.getEssayState()).isEqualTo("COMPLETE"),
                () -> assertThat(response.getStudentFilePath()).isEqualTo(filePath1),
                () -> assertThat(response.getTeacherFilePath()).isEqualTo(filePath2),
                () -> assertThat(comments.size()).isEqualTo(2),
                () -> assertThat(comments.get(0).getDetail()).isEqualTo("첨삭한 파일 첨부했습니다."),
                () -> assertThat(comments.get(1).getDetail()).isEqualTo("네 확인했습니다.")
        );
    }

    @Test
    void 리뷰된_완료상태의_첨삭_개별조회_테스트() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.COMPLETE, ReviewState.ON);
        Comment c1 = DemoDataFactory.createComment1(1L, t1, essay1);
        Comment c2 = DemoDataFactory.createComment2(2L, s1, essay1);
        Review r1 = DemoDataFactory.createReview1(1L, essay1, s1, t1);
        // when
        when(essayRepository.findById(1L)).thenReturn(Optional.of(essay1));

        String filePath1 = "http://s3-ap-northeast-2.amazonaws.com/sulsul/20230624.pdf";
        String filePath2 = "http://s3-ap-northeast-2.amazonaws.com/sulsul/20230625.pdf";
        when(fileRepository.getStudentEssayFile(1L, 1L))
                .thenReturn(Optional.of(File.builder()
                        .id(1L)
                        .filePath(filePath1)
                        .build()));
        when(fileRepository.getTeacherEssayFile(1L, 2L))
                .thenReturn(Optional.of(File.builder()
                        .id(2L)
                        .filePath(filePath2)
                        .build()));
        when(commentRepository.findAllByEssayId(1L)).thenReturn(List.of(c1, c2));
        when(reviewRepository.findByEssayId(1L)).thenReturn(Optional.of(r1));

        ReviewedEssayResponse response = essayService.getReviewedEssay(1L);
        List<CommentResponse> comments = response.getComments();
        // then
        assertAll(
                () -> assertThat(response.getUniv()).isEqualTo("홍익대"),
                () -> assertThat(response.getExamYear()).isEqualTo("2022"),
                () -> assertThat(response.getEssayType()).isEqualTo("수리"),
                () -> assertThat(response.getInquiry()).isEqualTo("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다."),
                () -> assertThat(response.getEssayState()).isEqualTo("COMPLETE"),
                () -> assertThat(response.getStudentFilePath()).isEqualTo(filePath1),
                () -> assertThat(response.getTeacherFilePath()).isEqualTo(filePath2),
                () -> assertThat(comments.size()).isEqualTo(2),
                () -> assertThat(comments.get(0).getDetail()).isEqualTo("첨삭한 파일 첨부했습니다."),
                () -> assertThat(comments.get(1).getDetail()).isEqualTo("네 확인했습니다."),
                () -> assertThat(response.getReview().getDetail()).isEqualTo("구체적으로 첨삭해주셔서 좋았어요."),
                () -> assertThat(response.getReview().getScore()).isEqualTo(5)
        );
    }

    @Test
    void 첨삭요청_수락_테스트() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.REQUEST, ReviewState.OFF);
        // stub
        when(essayRepository.findById(1L)).thenReturn(Optional.of(essay1));
        when(essayRepository.save(any(Essay.class))).then(returnsFirstArg());
        // when
        Essay accepted = essayService.acceptEssay(1L);
        // then
        assertAll(
                () -> assertThat(accepted.getUniv()).isEqualTo("홍익대"),
                () -> assertThat(accepted.getExamYear()).isEqualTo("2022"),
                () -> assertThat(accepted.getEssayType()).isEqualTo("수리"),
                () -> assertThat(accepted.getInquiry()).isEqualTo("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다."),
                () -> assertThat(accepted.getEssayState()).isEqualTo(EssayState.PROCEED),
                () -> assertThat(accepted.getReviewState()).isEqualTo(ReviewState.OFF)
        );
    }

    @Test
    void 첨삭요청_거절_테스트() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.REQUEST, ReviewState.OFF);
        RejectRequest request = new RejectRequest("시간상 첨삭이 불가능할 것 같습니다.");
        // stub
        when(essayRepository.findById(1L)).thenReturn(Optional.of(essay1));
        when(essayRepository.save(any(Essay.class))).then(returnsFirstArg());
        // when
        Essay rejected = essayService.rejectEssay(1L, request);
        // then
        assertAll(
                () -> assertThat(rejected.getUniv()).isEqualTo("홍익대"),
                () -> assertThat(rejected.getExamYear()).isEqualTo("2022"),
                () -> assertThat(rejected.getEssayType()).isEqualTo("수리"),
                () -> assertThat(rejected.getInquiry()).isEqualTo("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다."),
                () -> assertThat(rejected.getRejectDetail()).isEqualTo("시간상 첨삭이 불가능할 것 같습니다."),
                () -> assertThat(rejected.getEssayState()).isEqualTo(EssayState.REJECT),
                () -> assertThat(rejected.getReviewState()).isEqualTo(ReviewState.OFF)
        );
    }

    @Test
    void 첨삭완료_테스트() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.PROCEED, ReviewState.OFF);
        // stub
        when(essayRepository.findById(1L)).thenReturn(Optional.of(essay1));
        when(essayRepository.save(any(Essay.class))).then(returnsFirstArg());
        // when
        Essay completed = essayService.completeEssay(1L);
        // then
        assertAll(
                () -> assertThat(completed.getUniv()).isEqualTo("홍익대"),
                () -> assertThat(completed.getExamYear()).isEqualTo("2022"),
                () -> assertThat(completed.getEssayType()).isEqualTo("수리"),
                () -> assertThat(completed.getInquiry()).isEqualTo("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다."),
                () -> assertThat(completed.getEssayState()).isEqualTo(EssayState.COMPLETE),
                () -> assertThat(completed.getReviewState()).isEqualTo(ReviewState.OFF)
        );
    }
}