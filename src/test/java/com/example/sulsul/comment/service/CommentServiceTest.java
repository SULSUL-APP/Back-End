package com.example.sulsul.comment.service;

import com.example.sulsul.comment.dto.request.CommentRequest;
import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.comment.repository.CommentRepository;
import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.common.type.ReviewState;
import com.example.sulsul.essay.DemoDataFactory;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.repository.EssayRepository;
import com.example.sulsul.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private EssayRepository essayRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("첨삭에 작성된 댓글조회 테스트")
    void getCommentsTest() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.PROCEED, ReviewState.OFF);
        Comment c1 = DemoDataFactory.createComment1(1L, t1, essay1);
        Comment c2 = DemoDataFactory.createComment2(2L, s1, essay1);
        // stub
        when(commentRepository.findAllByEssayId(eq(1L))).thenReturn(List.of(c1, c2));
        // when
        List<Comment> comments = commentService.getComments(1L);
        // then
        assertAll(
                () -> assertThat(comments.size()).isEqualTo(2),
                () -> assertThat(comments.get(0).getDetail()).isEqualTo("첨삭한 파일 첨부했습니다."),
                () -> assertThat(comments.get(0).getUser().getName()).isEqualTo("임탁균"),
                () -> assertThat(comments.get(0).getUser().getEmail()).isEqualTo("sulsul@naver.com"),
                () -> assertThat(comments.get(1).getUser().getName()).isEqualTo("김경근"),
                () -> assertThat(comments.get(1).getUser().getEmail()).isEqualTo("sulsul@gmail.com"),
                () -> assertThat(comments.get(0).getEssay().getUniv()).isEqualTo("홍익대"),
                () -> assertThat(comments.get(0).getEssay().getExamYear()).isEqualTo("2022"),
                () -> assertThat(comments.get(0).getEssay().getEssayType()).isEqualTo("수리"),
                () -> assertThat(comments.get(0).getEssay().getEssayState()).isEqualTo(EssayState.PROCEED),
                () -> assertThat(comments.get(0).getEssay().getReviewState()).isEqualTo(ReviewState.OFF)
        );
    }

    @Test
    @DisplayName("댓글 작성 테스트")
    void createCommentTest() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.PROCEED, ReviewState.OFF);
        // stub
        when(essayRepository.findById(eq(1L))).thenReturn(Optional.of(essay1));
        when(commentRepository.save(any(Comment.class))).then(returnsFirstArg());
        // when
        CommentRequest request = new CommentRequest("댓글 작성 테스트");
        Comment comment = commentService.createComment(1L, s1, request);
        // then
        assertAll(
                () -> assertThat(comment.getDetail()).isEqualTo("댓글 작성 테스트"),
                () -> assertThat(comment.getUser().getName()).isEqualTo("김경근"),
                () -> assertThat(comment.getUser().getEmail()).isEqualTo("sulsul@gmail.com"),
                () -> assertThat(comment.getEssay().getUniv()).isEqualTo("홍익대"),
                () -> assertThat(comment.getEssay().getExamYear()).isEqualTo("2022"),
                () -> assertThat(comment.getEssay().getEssayType()).isEqualTo("수리")
        );
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateCommentTest() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.PROCEED, ReviewState.OFF);
        Comment comment1 = DemoDataFactory.createComment1(1L, s1, essay1);
        // stub
        when(commentRepository.findById(eq(1L))).thenReturn(Optional.of(comment1));
        when(commentRepository.save(any(Comment.class))).then(returnsFirstArg());
        // when
        CommentRequest request = new CommentRequest("댓글 수정 테스트");
        Comment comment = commentService.updateComment(1L, request);
        // then
        assertAll(
                () -> assertThat(comment.getDetail()).isEqualTo("댓글 수정 테스트"),
                () -> assertThat(comment.getUser().getName()).isEqualTo("김경근"),
                () -> assertThat(comment.getUser().getEmail()).isEqualTo("sulsul@gmail.com"),
                () -> assertThat(comment.getEssay().getUniv()).isEqualTo("홍익대"),
                () -> assertThat(comment.getEssay().getExamYear()).isEqualTo("2022"),
                () -> assertThat(comment.getEssay().getEssayType()).isEqualTo("수리")
        );
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteCommentTest() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.PROCEED, ReviewState.OFF);
        Comment comment1 = DemoDataFactory.createComment1(1L, s1, essay1);
        // stub
        when(commentRepository.findById(eq(1L))).thenReturn(Optional.of(comment1));
        doNothing().when(commentRepository).deleteById(eq(1L));
        // when
        commentService.deleteComment(1L);
        // then
        verify(commentRepository, times(1)).deleteById(eq(1L));
    }
}