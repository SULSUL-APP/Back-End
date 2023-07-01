package com.example.sulsul.comment.repository;

import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.common.type.*;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.repository.EssayRepository;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    private EssayRepository essayRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void 댓글생성_테스트() {
        // given
        User s1 = User.builder()
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

        userRepository.save(s1);
        userRepository.save(t1);

        Essay essay1 = Essay.builder()
                .univ("홍익대")
                .examYear("2022")
                .eType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayState(EssayState.PROCEED)
                .reviewState(ReviewState.OFF)
                .student(s1)
                .teacher(t1)
                .build();

        essayRepository.save(essay1);
        // when
        Comment c1 = Comment.builder()
                .user(t1)
                .essay(essay1)
                .detail("첨삭한 파일 첨부했습니다.")
                .build();

        Comment saved = commentRepository.save(c1);
        // then
        assertAll(
                () -> assertThat(saved.getDetail()).isEqualTo("첨삭한 파일 첨부했습니다."),
                () -> assertThat(saved.getEssay().getUniv()).isEqualTo("홍익대"),
                () -> assertThat(saved.getEssay().getExamYear()).isEqualTo("2022"),
                () -> assertThat(saved.getEssay().getEType()).isEqualTo("수리"),
                () -> assertThat(saved.getUser().getName()).isEqualTo("임탁균"),
                () -> assertThat(saved.getUser().getEmail()).isEqualTo("sulsul@naver.com")
        );
    }

    @Test
    void 댓글수정_테스트() {
        // given
        User s1 = User.builder()
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

        userRepository.save(s1);
        userRepository.save(t1);

        Essay essay1 = Essay.builder()
                .univ("홍익대")
                .examYear("2022")
                .eType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayState(EssayState.PROCEED)
                .reviewState(ReviewState.OFF)
                .student(s1)
                .teacher(t1)
                .build();

        essayRepository.save(essay1);
        // when
        Comment c1 = Comment.builder()
                .user(t1)
                .essay(essay1)
                .detail("첨삭한 파일 첨부했습니다.")
                .build();

        Comment saved = commentRepository.save(c1);
        saved.updateDetail("댓글 내용 수정");
        Comment updated = commentRepository.save(saved);
        // then
        assertAll(
                () -> assertThat(updated.getDetail()).isEqualTo("댓글 내용 수정"),
                () -> assertThat(updated.getEssay().getUniv()).isEqualTo("홍익대"),
                () -> assertThat(updated.getEssay().getExamYear()).isEqualTo("2022"),
                () -> assertThat(updated.getEssay().getEType()).isEqualTo("수리"),
                () -> assertThat(updated.getUser().getName()).isEqualTo("임탁균"),
                () -> assertThat(updated.getUser().getEmail()).isEqualTo("sulsul@naver.com")
        );
    }

    @Test
    void 댓글삭제_테스트() {
        // given
        User s1 = User.builder()
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

        userRepository.save(s1);
        userRepository.save(t1);

        Essay essay1 = Essay.builder()
                .univ("홍익대")
                .examYear("2022")
                .eType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayState(EssayState.PROCEED)
                .reviewState(ReviewState.OFF)
                .student(s1)
                .teacher(t1)
                .build();

        essayRepository.save(essay1);
        // when
        Comment c1 = Comment.builder()
                .user(t1)
                .essay(essay1)
                .detail("첨삭한 파일 첨부했습니다.")
                .build();

        Comment saved = commentRepository.save(c1);
        commentRepository.deleteById(saved.getId());
        // then
        boolean deleted = commentRepository.findById(saved.getId()).isEmpty();
        assertThat(deleted).isTrue();
    }

    @Test
    void 첨삭에_작성된_댓글목록_조회_테스트() {
        // given
        User s1 = User.builder()
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

        userRepository.save(s1);
        userRepository.save(t1);

        Essay essay1 = Essay.builder()
                .univ("홍익대")
                .examYear("2022")
                .eType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayState(EssayState.PROCEED)
                .reviewState(ReviewState.OFF)
                .student(s1)
                .teacher(t1)
                .build();

        essayRepository.save(essay1);
        // when
        Comment c1 = Comment.builder()
                .user(t1)
                .essay(essay1)
                .detail("첨삭한 파일 첨부했습니다.")
                .build();

        Comment c2 = Comment.builder()
                .user(s1)
                .essay(essay1)
                .detail("네 확인했습니다.")
                .build();

        commentRepository.save(c1);
        commentRepository.save(c2);
        List<Comment> comments = commentRepository.findAllByEssayId(essay1.getId());
        // then
        assertAll(
                () -> assertThat(comments.size()).isEqualTo(2),
                () -> assertThat(comments.get(0).getDetail()).isEqualTo("첨삭한 파일 첨부했습니다."),
                () -> assertThat(comments.get(1).getDetail()).isEqualTo("네 확인했습니다.")
        );
    }
}