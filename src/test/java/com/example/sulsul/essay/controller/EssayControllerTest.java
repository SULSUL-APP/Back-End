package com.example.sulsul.essay.controller;

import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.essay.DemoDataFactory;
import com.example.sulsul.essay.dto.request.CreateEssayRequest;
import com.example.sulsul.essay.dto.response.ProceedEssayResponse;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.common.type.ReviewState;
import com.example.sulsul.essay.service.EssayService;
import com.example.sulsul.file.entity.File;
import com.example.sulsul.file.service.FileService;
import com.example.sulsul.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        // stub
        when(essayService.createEssay(eq(1L), any(User.class), any(CreateEssayRequest.class)))
                .thenReturn(essay1);
        // when && then
        mockMvc.perform(multipart("/profiles/{profileId}/essay", 1L)
                        .file("essayFile", testFile.getBytes())
                        .param("univ", "홍익대")
                        .param("examYear", "2022")
                        .param("inquiry", "2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                        .param("eType", "수리"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.essayId").value(1L))
                .andExpect(jsonPath("$.teacher.name").value("임탁균"))
                .andExpect(jsonPath("$.teacher.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
                .andExpect(jsonPath("$.student.name").value("김경근"))
                .andExpect(jsonPath("$.student.email").value("sulsul@gmail.com"));
    }

    @Test
    @DisplayName("강사 첨삭파일 업로드 POST /essay/proceed/{essayId}/upload")
    void uploadTeacherEssayFileTest() throws Exception {
        // given
        User t1 = DemoDataFactory.createTeacher1(1L);
        User s1 = DemoDataFactory.createStudent1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.PROCEED, ReviewState.OFF);
        Comment c1 = DemoDataFactory.createComment1(1L, s1, essay1);
        Comment c2 = DemoDataFactory.createComment1(2L, t1, essay1);
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
                .andExpect(status().isCreated());
//                .andExpect(jsonPath("$.essayId").value(1L))
//                .andExpect(jsonPath("$.teacher.name").value("임탁균"))
//                .andExpect(jsonPath("$.teacher.email").value("sulsul@naver.com"))
//                .andExpect(jsonPath("$.teacher.catchPhrase").value("항상 최선을 다하겠습니다. 화이링"))
//                .andExpect(jsonPath("$.student.name").value("김경근"))
//                .andExpect(jsonPath("$.student.email").value("sulsul@gmail.com"));
    }

    @Test
    void getRequestEssays() {
    }

    @Test
    void getProceedEssays() {
    }

    @Test
    void getRejectEssays() {
    }

    @Test
    void getCompleteEssays() {
    }

    @Test
    void getRequestEssay() {
    }

    @Test
    void getRejectEssay() {
    }

    @Test
    void getProceedEssay() {
    }

    @Test
    void getCompleteEssay() {
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