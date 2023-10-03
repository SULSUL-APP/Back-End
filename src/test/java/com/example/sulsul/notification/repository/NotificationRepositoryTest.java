package com.example.sulsul.notification.repository;

import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.common.type.ReviewState;
import com.example.sulsul.essay.DemoDataFactory;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.repository.EssayRepository;
import com.example.sulsul.notification.entity.Notification;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EssayRepository essayRepository;

    @Test
    @DisplayName("알림조회 테스트")
    void 알림조회_테스트() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.REQUEST, ReviewState.OFF);

        String sname = s1.getName();
        Notification en1 = Notification.builder()
                .title("첨삭요청 알림")
                .body(sname + "님으로부터 첨삭요청이 들어왔습니다.")
                .user(t1) // 알림을 받는 유저
                .essay(essay1)
                .build();

        String tname = t1.getName();
        Notification en2 = Notification.builder()
                .title("첨삭거절 알림")
                .body(tname + "님이 첨삭요청을 거절했습니다.")
                .user(s1) // 알림을 받는 유저
                .essay(essay1)
                .build();

        Notification cn1 = new Notification("전체공지", "서버 점검이 예정되어 있습니다.");

        // when
        userRepository.save(s1);
        userRepository.save(t1);
        essayRepository.save(essay1);
        notificationRepository.save(cn1);
        notificationRepository.save(en1);
        notificationRepository.save(en2);

        // then
        var teacherNotifications = notificationRepository.findNotificationByUserId(t1.getId());
        var studentNotifications = notificationRepository.findNotificationByUserId(s1.getId());
        Notification n1 = teacherNotifications.get(0);
        Notification n2 = studentNotifications.get(0);

        assertAll(
                () -> assertThat(teacherNotifications.size()).isEqualTo(2),
                () -> assertThat(n1.getTitle()).isEqualTo("첨삭요청 알림"),
                () -> assertThat(n1.getBody()).isEqualTo(sname + "님으로부터 첨삭요청이 들어왔습니다."),
                () -> assertThat(studentNotifications.size()).isEqualTo(2),
                () -> assertThat(n2.getTitle()).isEqualTo("첨삭거절 알림"),
                () -> assertThat(n2.getBody()).isEqualTo(tname + "님이 첨삭요청을 거절했습니다.")
        );
    }

    @Test
    void 알림생성일_계산_테스트() {
        //2023-08-22T01:09:37.327784300
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start0 = LocalDateTime.of(2023, 8, 12, 1, 9, 37, 327784300);
        LocalDateTime start1 = LocalDateTime.of(2023, 8, 12, 12, 32, 22, 3333);
        LocalDateTime start2 = LocalDateTime.of(2023, 7, 12, 12, 32, 22, 3333);
        LocalDateTime start3 = LocalDateTime.of(2022, 11, 12, 12, 32, 22, 3333);
        long between0 = ChronoUnit.DAYS.between(start0, end);
        long between1 = ChronoUnit.DAYS.between(start1, end);
        long between2 = ChronoUnit.DAYS.between(start2, end);
        long between3 = ChronoUnit.DAYS.between(start3, end);
        System.out.println("between0 = " + between0);
        System.out.println("between1 = " + between1);
        System.out.println("between2 = " + between2);
        System.out.println("between3 = " + between3);
    }
}