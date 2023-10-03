package com.example.sulsul.comment.controller;

import com.example.sulsul.comment.dto.request.CommentRequest;
import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.comment.service.CommentService;
import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.common.type.ReviewState;
import com.example.sulsul.essay.DemoDataFactory;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("댓글조회 GET /essay/{essayId}/comments")
    void getCommentsTest() throws Exception {
        // given
        User t1 = DemoDataFactory.createTeacher1(1L);
        User s1 = DemoDataFactory.createStudent1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.PROCEED, ReviewState.OFF);
        Comment c1 = DemoDataFactory.createComment1(1L, t1, essay1);
        Comment c2 = DemoDataFactory.createComment2(2L, s1, essay1);
        // stub
        when(commentService.getComments(eq(1L))).thenReturn(List.of(c1, c2));
        // when && then
        mockMvc.perform(get("/essay/{essayId}/comments", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments[0].id").value(1L))
                .andExpect(jsonPath("$.comments[0].detail").value("첨삭한 파일 첨부했습니다."))
                .andExpect(jsonPath("$.comments[0].writer.name").value("임탁균"))
                .andExpect(jsonPath("$.comments[0].writer.email").value("sulsul@naver.com"))
                .andExpect(jsonPath("$.comments[1].id").value(2L))
                .andExpect(jsonPath("$.comments[1].detail").value("네 확인했습니다."))
                .andExpect(jsonPath("$.comments[1].writer.name").value("김경근"))
                .andExpect(jsonPath("$.comments[1].writer.email").value("sulsul@gmail.com"));
    }

    @Test
    @DisplayName("댓글생성 POST /essay/{essayId}/comments")
    void createCommentTest() throws Exception {
        // given
        User t1 = DemoDataFactory.createTeacher1(1L);
        User s1 = DemoDataFactory.createStudent1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.PROCEED, ReviewState.OFF);
        Comment c1 = DemoDataFactory.createComment1(1L, t1, essay1);
        CommentRequest request = new CommentRequest("첨삭한 파일 첨부했습니다.");
        String content = objectMapper.writeValueAsString(request);
        // stub
        when(commentService.createComment(eq(1L), any(User.class), any(CommentRequest.class))).thenReturn(c1);
        // when && then
        mockMvc.perform(post("/essay/{essayId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.detail").value("첨삭한 파일 첨부했습니다."))
                .andExpect(jsonPath("$.writer.name").value("임탁균"))
                .andExpect(jsonPath("$.writer.email").value("sulsul@naver.com"));
    }

    @Test
    @DisplayName("댓글생성 예외처리 테스트 POST /essay/{essayId}/comments")
    void createCommentWithNoRequestTest() throws Exception {
        // given
        User t1 = DemoDataFactory.createTeacher1(1L);
        User s1 = DemoDataFactory.createStudent1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.PROCEED, ReviewState.OFF);
        CommentRequest request = new CommentRequest("첨");
        String content = objectMapper.writeValueAsString(request);
        // when && then
        mockMvc.perform(post("/essay/{essayId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print());
    }

    @Test
    @DisplayName("댓글수정 PUT /comments/{commentId}")
    void updateCommentTest() throws Exception {
        // given
        User t1 = DemoDataFactory.createTeacher1(1L);
        User s1 = DemoDataFactory.createStudent1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.PROCEED, ReviewState.OFF);
        Comment c1 = DemoDataFactory.createComment1(1L, t1, essay1);
        c1.updateDetail("댓글 수정 테스트");
        CommentRequest request = new CommentRequest("댓글 수정 테스트");
        String content = objectMapper.writeValueAsString(request);
        // stub
        when(commentService.updateComment(eq(1L), any(CommentRequest.class))).thenReturn(c1);
        // when && then
        mockMvc.perform(put("/comments/{commentId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.detail").value("댓글 수정 테스트"))
                .andExpect(jsonPath("$.writer.name").value("임탁균"))
                .andExpect(jsonPath("$.writer.email").value("sulsul@naver.com"));
    }

    @Test
    @DisplayName("댓글삭제 DELETE /comments/{commentId}")
    void deleteCommentTest() throws Exception {
        // stub
        doNothing().when(commentService).deleteComment(eq(1L));
        // when && then
        mockMvc.perform(delete("/comments/{commentId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 삭제 성공"));
    }
}