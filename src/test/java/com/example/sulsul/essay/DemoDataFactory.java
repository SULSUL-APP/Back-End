package com.example.sulsul.essay;

import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.common.type.EType;
import com.example.sulsul.common.type.LoginType;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.common.type.ReviewState;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.user.entity.User;

public class DemoDataFactory {
    public static User createStudent1(long id) {
        return User.builder()
                .id(id)
                .name("김경근")
                .email("sulsul@gmail.com")
                .userType(UType.STUDENT)
                .essayType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .build();
    }

    public static User createStudent2(long id) {
        return User.builder()
                .id(id)
                .name("류동완")
                .email("sulsul@g.hongik.ac.kr")
                .userType(UType.STUDENT)
                .essayType(EType.SOCIETY)
                .loginType(LoginType.APPLE)
                .build();
    }

    public static User createTeacher1(long id) {
        return User.builder()
                .id(id)
                .name("임탁균")
                .email("sulsul@naver.com")
                .userType(UType.TEACHER)
                .essayType(EType.NATURE)
                .loginType(LoginType.KAKAO)
                .catchPhrase("항상 최선을 다하겠습니다. 화이링")
                .build();
    }

    public static User createTeacher2(long id) {
        return User.builder()
                .id(id)
                .name("전용수")
                .email("smc@gmail.com")
                .userType(UType.TEACHER)
                .essayType(EType.SOCIETY)
                .loginType(LoginType.KAKAO)
                .catchPhrase("항상 최선을 다하겠습니다.")
                .build();
    }

    public static Essay createEssay1(long id, User student, User teacher,
                                     EssayState essayState, ReviewState reviewState) {
        return Essay.builder()
                .id(id)
                .univ("홍익대")
                .examYear("2022")
                .essayType("수리")
                .inquiry("2022년 수리논술 3번 문제까지 첨삭 부탁드립니다.")
                .essayState(essayState)
                .reviewState(reviewState)
                .student(student)
                .teacher(teacher)
                .build();
    }

    public static Essay createEssay2(long id, User student, User teacher,
                                     EssayState essayState, ReviewState reviewState) {
        return Essay.builder()
                .id(id)
                .univ("홍익대")
                .examYear("2023")
                .essayType("인문사회")
                .inquiry("2023년 인문 첨삭 부탁드립니다.")
                .essayState(essayState)
                .reviewState(reviewState)
                .student(student)
                .teacher(teacher)
                .build();
    }

    public static Comment createComment1(long id, User user, Essay essay) {
        return Comment.builder()
                .id(id)
                .user(user)
                .essay(essay)
                .detail("첨삭한 파일 첨부했습니다.")
                .build();
    }

    public static Comment createComment2(long id, User user, Essay essay) {
        return Comment.builder()
                .id(id)
                .user(user)
                .essay(essay)
                .detail("네 확인했습니다.")
                .build();
    }

    public static Review createReview1(long id, Essay essay, User student, User teacher) {
        return Review.builder()
                .id(id)
                .detail("구체적으로 첨삭해주셔서 좋았어요.")
                .score(5)
                .essay(essay)
                .student(student)
                .teacher(teacher)
                .build();
    }

    public static Review createReview2(long id, Essay essay, User student, User teacher) {
        return Review.builder()
                .id(id)
                .detail("바쁘셔서 그런지 의사소통이 잘 안되는 것 같았어요.")
                .score(2)
                .essay(essay)
                .student(student)
                .teacher(teacher)
                .build();
    }
}