package com.example.sulsul.essay.controller;

import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.common.type.ReviewState;
import com.example.sulsul.essay.DemoDataFactory;
import com.example.sulsul.essay.dto.request.CreateEssayRequest;
import com.example.sulsul.essay.dto.request.RejectRequest;
import com.example.sulsul.essay.dto.response.*;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.service.EssayService;
import com.example.sulsul.file.service.FileService;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {EssayController.class})
class EssayControllerTest {

    @MockBean
    private EssayService essayService;

    @MockBean
    private FileService fileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(path);
        return new MockMultipartFile(fileName, fileName + "." + contentType, contentType, fileInputStream);
    }

    @Test
    @DisplayName("첨삭요청 POST /profiles/{profileId}/essay")
    void createEssayTest() throws Exception {
        // given
        User t1 = DemoDataFactory.createTeacher1(1L);
        User s1 = DemoDataFactory.createStudent1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.REQUEST, ReviewState.OFF);
        // 테스트용 첨삭파일 생성
        String fileName = "test";
        String contentType = "pdf";
        String filePath = "src/test/resources/pdf/test.pdf";
        MockMultipartFile testFile = getMockMultipartFile(fileName, contentType, filePath);
        // 학생이 업로드한 첨삭파일의 파일경로
        String studentFilePath = "https://sulsul.s3.ap-northeast-2.amazonaws.com/files/314a32f7_sulsul.pdf";
        // stub
        when(essayService.createEssay(eq(1L), any(User.class), any(CreateEssayRequest.class)))
                .thenReturn(essay1);
        when(essayService.getEssayResponseWithStudentFile(eq(1L)))
                .thenReturn(new RequestEssayResponse(essay1, studentFilePath));
        // when && then
        mockMvc.perform(multipart("/profiles/{profileId}/essay", 1L)
                        .file("essayFile", testFile.getBytes())
                        .param("univ", "홍익대")
                        .param("examYear", "2022")
                        .param("inquiry", "2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                        .param("eType", "수리"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.teacher.name").value("임탁균"))
                .andExpect(jsonPath("$.teacher.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
                .andExpect(jsonPath("$.student.name").value("김경근"))
                .andExpect(jsonPath("$.student.email").value("sulsul@gmail.com"))
                .andExpect(jsonPath("$.studentFilePath").value(studentFilePath));
    }

    @Test
    @DisplayName("강사 첨삭파일 업로드 POST /essay/proceed/{essayId}/upload")
    void uploadTeacherEssayFileTest() throws Exception {
        // given
        User t1 = DemoDataFactory.createTeacher1(1L);
        User s1 = DemoDataFactory.createStudent1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.PROCEED, ReviewState.OFF);
        Comment c1 = DemoDataFactory.createComment1(1L, s1, essay1);
        Comment c2 = DemoDataFactory.createComment2(2L, t1, essay1);
        List<Comment> comments = List.of(c1, c2);
        // 테스트용 첨삭파일 생성
        String fileName = "test";
        String contentType = "pdf";
        String filePath = "src/test/resources/pdf/test.pdf";
        MockMultipartFile testFile = getMockMultipartFile(fileName, contentType, filePath);
        // 업로드 된 첨삭파일들의 파일경로
        String teacherFilePath = "https://sulsul.s3.ap-northeast-2.amazonaws.com/files/751b44f7_sulsul.pdf";
        String studentFilePath = "https://sulsul.s3.ap-northeast-2.amazonaws.com/files/314a32f7_sulsul.pdf";
        // stub
        when(essayService.getEssayById(eq(1L))).thenReturn(essay1);
        when(essayService.getEssayResponseWithFilePaths(eq(1L)))
                .thenReturn(new ProceedEssayResponse(essay1, studentFilePath, teacherFilePath, comments));
        // when && then
        mockMvc.perform(multipart("/essay/proceed/{essayId}/upload", 1L)
                        .file("essayFile", testFile.getBytes()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.teacher.name").value("임탁균"))
                .andExpect(jsonPath("$.teacher.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
                .andExpect(jsonPath("$.student.name").value("김경근"))
                .andExpect(jsonPath("$.student.email").value("sulsul@gmail.com"))
                .andExpect(jsonPath("$.studentFilePath").value(studentFilePath))
                .andExpect(jsonPath("$.teacherFilePath").value(teacherFilePath))
                .andExpect(jsonPath("$.comments[0].commentId").value(1L))
                .andExpect(jsonPath("$.comments[0].user.name").value("김경근"))
                .andExpect(jsonPath("$.comments[1].commentId").value(2L))
                .andExpect(jsonPath("$.comments[1].user.name").value("임탁균"));
    }

    @Test
    @DisplayName("첨삭요청 목록 조회 GET /essay/request")
    void getRequestEssaysTest() throws Exception {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        User t2 = DemoDataFactory.createTeacher2(3L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.REQUEST, ReviewState.OFF);
        Essay essay2 = DemoDataFactory.createEssay2(2L, s1, t2, EssayState.REQUEST, ReviewState.OFF);
        // stub
        when(essayService.getEssaysByUser(any(User.class), eq(EssayState.REQUEST))).thenReturn(List.of(essay1, essay2));
        // when && then
        mockMvc.perform(get("/essay/request"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.essays[0].id").value(1L))
                .andExpect(jsonPath("$.essays[0].univ").value("홍익대"))
                .andExpect(jsonPath("$.essays[0].examYear").value("2022"))
                .andExpect(jsonPath("$.essays[0].teacher.name").value("임탁균"))
                .andExpect(jsonPath("$.essays[0].teacher.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.essays[0].teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
                .andExpect(jsonPath("$.essays[0].student.name").value("김경근"))
                .andExpect(jsonPath("$.essays[0].student.email").value("sulsul@gmail.com"))
                .andExpect(jsonPath("$.essays[1].id").value(2L))
                .andExpect(jsonPath("$.essays[1].univ").value("홍익대"))
                .andExpect(jsonPath("$.essays[1].examYear").value("2023"))
                .andExpect(jsonPath("$.essays[1].teacher.name").value("전용수"))
                .andExpect(jsonPath("$.essays[1].teacher.email").value("smc@gmail.com"))
                .andExpect(jsonPath("$.essays[1].teacher.catchPhrase").value("항상 최선을 다하겠습니다."))
                .andExpect(jsonPath("$.essays[1].student.name").value("김경근"))
                .andExpect(jsonPath("$.essays[1].student.email").value("sulsul@gmail.com"));
    }

    @Test
    @DisplayName("진행중인 첨삭목록 조회 GET /essay/proceed")
    void getProceedEssaysTest() throws Exception {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        User t2 = DemoDataFactory.createTeacher2(3L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.PROCEED, ReviewState.OFF);
        Essay essay2 = DemoDataFactory.createEssay2(2L, s1, t2, EssayState.PROCEED, ReviewState.OFF);
        // stub
        when(essayService.getEssaysByUser(any(User.class), eq(EssayState.PROCEED))).thenReturn(List.of(essay1, essay2));
        // when && then
        mockMvc.perform(get("/essay/proceed"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.essays[0].id").value(1L))
                .andExpect(jsonPath("$.essays[0].univ").value("홍익대"))
                .andExpect(jsonPath("$.essays[0].examYear").value("2022"))
                .andExpect(jsonPath("$.essays[0].teacher.name").value("임탁균"))
                .andExpect(jsonPath("$.essays[0].teacher.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.essays[0].teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
                .andExpect(jsonPath("$.essays[0].student.name").value("김경근"))
                .andExpect(jsonPath("$.essays[0].student.email").value("sulsul@gmail.com"))
                .andExpect(jsonPath("$.essays[1].id").value(2L))
                .andExpect(jsonPath("$.essays[1].univ").value("홍익대"))
                .andExpect(jsonPath("$.essays[1].examYear").value("2023"))
                .andExpect(jsonPath("$.essays[1].teacher.name").value("전용수"))
                .andExpect(jsonPath("$.essays[1].teacher.email").value("smc@gmail.com"))
                .andExpect(jsonPath("$.essays[1].teacher.catchPhrase").value("항상 최선을 다하겠습니다."))
                .andExpect(jsonPath("$.essays[1].student.name").value("김경근"))
                .andExpect(jsonPath("$.essays[1].student.email").value("sulsul@gmail.com"));
    }

    @Test
    @DisplayName("거절된 첨삭목록 조회 GET /essay/reject")
    void getRejectEssaysTest() throws Exception {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        User t2 = DemoDataFactory.createTeacher2(3L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.REJECT, ReviewState.OFF);
        Essay essay2 = DemoDataFactory.createEssay2(2L, s1, t2, EssayState.REJECT, ReviewState.OFF);
        // stub
        when(essayService.getEssaysByUser(any(User.class), eq(EssayState.REJECT))).thenReturn(List.of(essay1, essay2));
        // when && then
        mockMvc.perform(get("/essay/reject"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.essays[0].id").value(1L))
                .andExpect(jsonPath("$.essays[0].univ").value("홍익대"))
                .andExpect(jsonPath("$.essays[0].examYear").value("2022"))
                .andExpect(jsonPath("$.essays[0].teacher.name").value("임탁균"))
                .andExpect(jsonPath("$.essays[0].teacher.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.essays[0].teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
                .andExpect(jsonPath("$.essays[0].student.name").value("김경근"))
                .andExpect(jsonPath("$.essays[0].student.email").value("sulsul@gmail.com"))
                .andExpect(jsonPath("$.essays[1].id").value(2L))
                .andExpect(jsonPath("$.essays[1].univ").value("홍익대"))
                .andExpect(jsonPath("$.essays[1].examYear").value("2023"))
                .andExpect(jsonPath("$.essays[1].teacher.name").value("전용수"))
                .andExpect(jsonPath("$.essays[1].teacher.email").value("smc@gmail.com"))
                .andExpect(jsonPath("$.essays[1].teacher.catchPhrase").value("항상 최선을 다하겠습니다."))
                .andExpect(jsonPath("$.essays[1].student.name").value("김경근"))
                .andExpect(jsonPath("$.essays[1].student.email").value("sulsul@gmail.com"));
    }

    @Test
    @DisplayName("완료된 첨삭목록 조회 GET /essay/complete")
    void getCompleteEssaysTest() throws Exception {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        User t2 = DemoDataFactory.createTeacher2(3L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.COMPLETE, ReviewState.OFF);
        Essay essay2 = DemoDataFactory.createEssay2(2L, s1, t2, EssayState.COMPLETE, ReviewState.OFF);
        // stub
        when(essayService.getEssaysByUser(any(User.class), eq(EssayState.COMPLETE))).thenReturn(List.of(essay1, essay2));
        // when && then
        mockMvc.perform(get("/essay/complete"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.essays[0].id").value(1L))
                .andExpect(jsonPath("$.essays[0].univ").value("홍익대"))
                .andExpect(jsonPath("$.essays[0].examYear").value("2022"))
                .andExpect(jsonPath("$.essays[0].teacher.name").value("임탁균"))
                .andExpect(jsonPath("$.essays[0].teacher.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.essays[0].teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
                .andExpect(jsonPath("$.essays[0].student.name").value("김경근"))
                .andExpect(jsonPath("$.essays[0].student.email").value("sulsul@gmail.com"))
                .andExpect(jsonPath("$.essays[1].id").value(2L))
                .andExpect(jsonPath("$.essays[1].univ").value("홍익대"))
                .andExpect(jsonPath("$.essays[1].examYear").value("2023"))
                .andExpect(jsonPath("$.essays[1].teacher.name").value("전용수"))
                .andExpect(jsonPath("$.essays[1].teacher.email").value("smc@gmail.com"))
                .andExpect(jsonPath("$.essays[1].teacher.catchPhrase").value("항상 최선을 다하겠습니다."))
                .andExpect(jsonPath("$.essays[1].student.name").value("김경근"))
                .andExpect(jsonPath("$.essays[1].student.email").value("sulsul@gmail.com"));
    }

    @Test
    @DisplayName("요청상태의 첨삭 개별조회 GET /essay/request/{essayId}")
    void getRequestEssayTest() throws Exception {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.REQUEST, ReviewState.OFF);
        // stub
        String studentFilePath = "https://sulsul.s3.ap-northeast-2.amazonaws.com/files/314a32f7_sulsul.pdf";
        when(essayService.getEssayResponseWithStudentFile(eq(1L)))
                .thenReturn(new RequestEssayResponse(essay1, studentFilePath));
        // when && then
        mockMvc.perform(get("/essay/request/{essayId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.univ").value("홍익대"))
                .andExpect(jsonPath("$.examYear").value("2022"))
                .andExpect(jsonPath("$.essayState").value("REQUEST"))
                .andExpect(jsonPath("$.studentFilePath").value(studentFilePath))
                .andExpect(jsonPath("$.teacher.name").value("임탁균"))
                .andExpect(jsonPath("$.teacher.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
                .andExpect(jsonPath("$.student.name").value("김경근"))
                .andExpect(jsonPath("$.student.email").value("sulsul@gmail.com"));
    }

    @Test
    @DisplayName("거절상태의 첨삭 개별조회 GET /essay/reject/{essayId}")
    void getRejectEssayTest() throws Exception {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.REJECT, ReviewState.OFF);
        String rejectDetail = "일정상 첨삭이 불가능할 것 같습니다.";
        essay1.updateRejectDetail(rejectDetail);
        // stub
        String studentFilePath = "https://sulsul.s3.ap-northeast-2.amazonaws.com/files/314a32f7_sulsul.pdf";
        when(essayService.getEssayResponseWithStudentFile(eq(1L)))
                .thenReturn(new RejectEssayResponse(essay1, studentFilePath));
        // when && then
        mockMvc.perform(get("/essay/reject/{essayId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.univ").value("홍익대"))
                .andExpect(jsonPath("$.examYear").value("2022"))
                .andExpect(jsonPath("$.essayState").value("REJECT"))
                .andExpect(jsonPath("$.rejectDetail").value(rejectDetail))
                .andExpect(jsonPath("$.studentFilePath").value(studentFilePath))
                .andExpect(jsonPath("$.teacher.name").value("임탁균"))
                .andExpect(jsonPath("$.teacher.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
                .andExpect(jsonPath("$.student.name").value("김경근"))
                .andExpect(jsonPath("$.student.email").value("sulsul@gmail.com"));
    }

    @Test
    @DisplayName("진행상태의 첨삭 개별조회 GET /essay/proceed/{essayId}")
    void getProceedEssayTest() throws Exception {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.PROCEED, ReviewState.OFF);
        Comment c1 = DemoDataFactory.createComment1(1L, s1, essay1);
        Comment c2 = DemoDataFactory.createComment2(2L, t1, essay1);
        List<Comment> comments = List.of(c1, c2);
        // stub
        String teacherFilePath = "https://sulsul.s3.ap-northeast-2.amazonaws.com/files/751b44f7_sulsul.pdf";
        String studentFilePath = "https://sulsul.s3.ap-northeast-2.amazonaws.com/files/314a32f7_sulsul.pdf";
        when(essayService.getEssayResponseWithFilePaths(eq(1L)))
                .thenReturn(new ProceedEssayResponse(essay1, studentFilePath, teacherFilePath, comments));
        // when && then
        mockMvc.perform(get("/essay/proceed/{essayId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.univ").value("홍익대"))
                .andExpect(jsonPath("$.examYear").value("2022"))
                .andExpect(jsonPath("$.essayState").value("PROCEED"))
                .andExpect(jsonPath("$.studentFilePath").value(studentFilePath))
                .andExpect(jsonPath("$.teacherFilePath").value(teacherFilePath))
                .andExpect(jsonPath("$.teacher.name").value("임탁균"))
                .andExpect(jsonPath("$.teacher.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
                .andExpect(jsonPath("$.student.name").value("김경근"))
                .andExpect(jsonPath("$.student.email").value("sulsul@gmail.com"))
                .andExpect(jsonPath("$.comments[0].commentId").value(1L))
                .andExpect(jsonPath("$.comments[0].user.name").value("김경근"))
                .andExpect(jsonPath("$.comments[1].commentId").value(2L))
                .andExpect(jsonPath("$.comments[1].user.name").value("임탁균"));
    }

    @Test
    @DisplayName("리뷰가 작성된 완료상태의 첨삭 개별조회 GET /essay/complete/{essayId}")
    void getCompleteEssayWithReviewTest() throws Exception {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.COMPLETE, ReviewState.ON);
        Comment c1 = DemoDataFactory.createComment1(1L, s1, essay1);
        Comment c2 = DemoDataFactory.createComment2(2L, t1, essay1);
        List<Comment> comments = List.of(c1, c2);
        Review review = DemoDataFactory.createReview1(1L, essay1, s1, t1);
        // stub
        String teacherFilePath = "https://sulsul.s3.ap-northeast-2.amazonaws.com/files/751b44f7_sulsul.pdf";
        String studentFilePath = "https://sulsul.s3.ap-northeast-2.amazonaws.com/files/314a32f7_sulsul.pdf";
        when(essayService.getEssayResponseWithFilePaths(eq(1L)))
                .thenReturn(new ReviewedEssayResponse(essay1, studentFilePath, teacherFilePath, comments, review));
        when(essayService.checkEssayReviewState(eq(1L))).thenReturn(true);
        // when && then
        mockMvc.perform(get("/essay/complete/{essayId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.univ").value("홍익대"))
                .andExpect(jsonPath("$.examYear").value("2022"))
                .andExpect(jsonPath("$.essayState").value("COMPLETE"))
                .andExpect(jsonPath("$.studentFilePath").value(studentFilePath))
                .andExpect(jsonPath("$.teacherFilePath").value(teacherFilePath))
                .andExpect(jsonPath("$.teacher.name").value("임탁균"))
                .andExpect(jsonPath("$.teacher.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
                .andExpect(jsonPath("$.student.name").value("김경근"))
                .andExpect(jsonPath("$.student.email").value("sulsul@gmail.com"))
                .andExpect(jsonPath("$.comments[0].commentId").value(1L))
                .andExpect(jsonPath("$.comments[0].user.name").value("김경근"))
                .andExpect(jsonPath("$.comments[1].commentId").value(2L))
                .andExpect(jsonPath("$.comments[1].user.name").value("임탁균"))
                .andExpect(jsonPath("$.review.id").value(1L))
                .andExpect(jsonPath("$.review.detail").value("구체적으로 첨삭해주셔서 좋았어요."))
                .andExpect(jsonPath("$.review.score").value(5))
                .andExpect(jsonPath("$.review.writer.name").value("김경근"));
    }

    @Test
    @DisplayName("리뷰가 작성되지 않은 완료상태의 첨삭 개별조회 GET /essay/complete/{essayId}")
    void getCompleteEssayWithNoReviewTest() throws Exception {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.COMPLETE, ReviewState.OFF);
        Comment c1 = DemoDataFactory.createComment1(1L, s1, essay1);
        Comment c2 = DemoDataFactory.createComment2(2L, t1, essay1);
        List<Comment> comments = List.of(c1, c2);
        // stub
        String teacherFilePath = "https://sulsul.s3.ap-northeast-2.amazonaws.com/files/751b44f7_sulsul.pdf";
        String studentFilePath = "https://sulsul.s3.ap-northeast-2.amazonaws.com/files/314a32f7_sulsul.pdf";
        when(essayService.getEssayResponseWithFilePaths(eq(1L)))
                .thenReturn(new CompletedEssayResponse(essay1, studentFilePath, teacherFilePath, comments));
        when(essayService.checkEssayReviewState(eq(1L))).thenReturn(false);
        // when && then
        mockMvc.perform(get("/essay/complete/{essayId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.univ").value("홍익대"))
                .andExpect(jsonPath("$.examYear").value("2022"))
                .andExpect(jsonPath("$.essayState").value("COMPLETE"))
                .andExpect(jsonPath("$.studentFilePath").value(studentFilePath))
                .andExpect(jsonPath("$.teacherFilePath").value(teacherFilePath))
                .andExpect(jsonPath("$.teacher.name").value("임탁균"))
                .andExpect(jsonPath("$.teacher.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
                .andExpect(jsonPath("$.student.name").value("김경근"))
                .andExpect(jsonPath("$.student.email").value("sulsul@gmail.com"))
                .andExpect(jsonPath("$.comments[0].commentId").value(1L))
                .andExpect(jsonPath("$.comments[0].user.name").value("김경근"))
                .andExpect(jsonPath("$.comments[1].commentId").value(2L))
                .andExpect(jsonPath("$.comments[1].user.name").value("임탁균"));
    }

    @Test
    @DisplayName("첨삭요청 수락 PUT /essay/{essayId}/accept")
    void acceptEssay() throws Exception {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.PROCEED, ReviewState.OFF);
        // stub
        when(essayService.acceptEssay(eq(1L))).thenReturn(essay1);
        // when && then
        mockMvc.perform(put("/essay/{essayId}/accept", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("첨삭요청이 수락되었습니다."))
                .andExpect(jsonPath("$.essay.id").value(1L))
                .andExpect(jsonPath("$.essay.univ").value("홍익대"))
                .andExpect(jsonPath("$.essay.examYear").value("2022"))
                .andExpect(jsonPath("$.essay.essayState").value("PROCEED"))
                .andExpect(jsonPath("$.essay.teacher.name").value("임탁균"))
                .andExpect(jsonPath("$.essay.teacher.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.essay.teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
                .andExpect(jsonPath("$.essay.student.name").value("김경근"))
                .andExpect(jsonPath("$.essay.student.email").value("sulsul@gmail.com"));
    }

    @Test
    @DisplayName("첨삭요청 거절 PUT /essay/{essayId}/reject")
    void rejectEssay() throws Exception {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.REJECT, ReviewState.OFF);
        String rejectDetail = "일정상 첨삭이 불가능할 것 같습니다.";
        essay1.updateRejectDetail(rejectDetail);
        // stub
        when(essayService.rejectEssay(eq(1L), any(RejectRequest.class))).thenReturn(essay1);
        // when && then
        String json = objectMapper.writeValueAsString(new RejectRequest(rejectDetail));
        mockMvc.perform(put("/essay/{essayId}/reject", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("첨삭요청이 거절되었습니다."))
                .andExpect(jsonPath("$.essay.id").value(1L))
                .andExpect(jsonPath("$.essay.univ").value("홍익대"))
                .andExpect(jsonPath("$.essay.examYear").value("2022"))
                .andExpect(jsonPath("$.essay.essayState").value("REJECT"))
                .andExpect(jsonPath("$.essay.teacher.name").value("임탁균"))
                .andExpect(jsonPath("$.essay.teacher.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.essay.teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
                .andExpect(jsonPath("$.essay.student.name").value("김경근"))
                .andExpect(jsonPath("$.essay.student.email").value("sulsul@gmail.com"));
//                .andExpect(jsonPath("$.essay.rejectDetail").value(rejectDetail)); // EssayResponse에는 rejectDetail이 포함되지 않음
    }

    @Test
    @DisplayName("첨삭완료처리 PUT /essay/{essayId}/complete")
    void completeEssay() throws Exception {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.COMPLETE, ReviewState.OFF);
        // stub
        when(essayService.completeEssay(eq(1L))).thenReturn(essay1);
        // when && then
        mockMvc.perform(put("/essay/{essayId}/complete", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("첨삭이 완료되었습니다."))
                .andExpect(jsonPath("$.essay.id").value(1L))
                .andExpect(jsonPath("$.essay.univ").value("홍익대"))
                .andExpect(jsonPath("$.essay.examYear").value("2022"))
                .andExpect(jsonPath("$.essay.essayState").value("COMPLETE"))
                .andExpect(jsonPath("$.essay.teacher.name").value("임탁균"))
                .andExpect(jsonPath("$.essay.teacher.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.essay.teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
                .andExpect(jsonPath("$.essay.student.name").value("김경근"))
                .andExpect(jsonPath("$.essay.student.email").value("sulsul@gmail.com"));
    }
}