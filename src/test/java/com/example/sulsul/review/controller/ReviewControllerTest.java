package com.example.sulsul.review.controller;

import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.common.type.ReviewState;
import com.example.sulsul.essay.DemoDataFactory;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.review.dto.request.ReviewRequest;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.review.service.ReviewService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @MockBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("리뷰작성 POST /essay/{essayId}/reviews")
    void createReviewTest() throws Exception {
        // given
        User t1 = DemoDataFactory.createTeacher1(1L);
        User s1 = DemoDataFactory.createStudent1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.COMPLETE, ReviewState.OFF);
        Review review = DemoDataFactory.createReview1(1L, essay1, s1, t1);
        // stub
        ReviewRequest request = new ReviewRequest("구체적으로 첨삭해주셔서 좋았어요.", 5);
        String content = objectMapper.writeValueAsString(request);
        when(reviewService.createReview(eq(1L), any(User.class), any(ReviewRequest.class)))
                .thenReturn(review);
        // when && then
        mockMvc.perform(post("/essay/{essayId}/reviews", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.detail").value("구체적으로 첨삭해주셔서 좋았어요."))
                .andExpect(jsonPath("$.score").value(5))
                .andExpect(jsonPath("$.writer.name").value("김경근"))
                .andExpect(jsonPath("$.writer.email").value("sulsul@gmail.com"));
    }

    @Test
    @DisplayName("강사에게 작성된 리뷰목록 조회 GET /profiles/{profileId}/reviews\"")
    void getReviewsTest() throws Exception {
        // given
        User t1 = DemoDataFactory.createTeacher1(1L);
        User s1 = DemoDataFactory.createStudent1(2L);
        User s2 = DemoDataFactory.createStudent2(3L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.COMPLETE, ReviewState.OFF);
        Essay essay2 = DemoDataFactory.createEssay1(1L, s2, t1, EssayState.COMPLETE, ReviewState.OFF);
        Review review1 = DemoDataFactory.createReview1(1L, essay1, s1, t1);
        Review review2 = DemoDataFactory.createReview2(2L, essay2, s2, t1);
        // stub
        when(reviewService.getReviews(eq(1L))).thenReturn(List.of(review1, review2));
        // when && then
        mockMvc.perform(get("/profiles/{profileId}/reviews", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviews[0].detail").value("구체적으로 첨삭해주셔서 좋았어요."))
                .andExpect(jsonPath("$.reviews[0].score").value(5))
                .andExpect(jsonPath("$.reviews[0].writer.name").value("김경근"))
                .andExpect(jsonPath("$.reviews[0].writer.email").value("sulsul@gmail.com"))
                .andExpect(jsonPath("$.reviews[1].detail").value("바쁘셔서 그런지 의사소통이 잘 안되는 것 같았어요."))
                .andExpect(jsonPath("$.reviews[1].score").value(2))
                .andExpect(jsonPath("$.reviews[1].writer.name").value("류동완"))
                .andExpect(jsonPath("$.reviews[1].writer.email").value("sulsul@g.hongik.ac.kr"));
    }
}