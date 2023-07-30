package com.example.sulsul.notification.repository;

import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.common.type.ReviewState;
import com.example.sulsul.essay.DemoDataFactory;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.repository.EssayRepository;
import com.example.sulsul.notification.entity.CommonNotification;
import com.example.sulsul.notification.entity.EssayNotification;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
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
    @DisplayName("첨삭알림 전체조회 테스트")
    void findEssayNotificationByUserTest() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.REQUEST, ReviewState.OFF);

        String sname = s1.getName();
        EssayNotification essayNoti1 = EssayNotification.builder()
                .title("첨삭요청 알림")
                .body(sname + "님으로부터 첨삭요청이 들어왔습니다.")
                .user(t1) // 알림을 받는 유저
                .essay(essay1)
                .build();

        String tname = t1.getName();
        EssayNotification essayNoti2 = EssayNotification.builder()
                .title("첨삭거절 알림")
                .body(tname + "님이 첨삭요청을 거절했습니다.")
                .user(s1) // 알림을 받는 유저
                .essay(essay1)
                .build();

        CommonNotification commonNoti = CommonNotification.builder()
                .title("전체공지")
                .body("서버 점검이 예정되어 있습니다.")
                .build();

        // when
        userRepository.save(s1);
        userRepository.save(t1);
        essayRepository.save(essay1);
        notificationRepository.save(essayNoti1);
        notificationRepository.save(essayNoti2);
        notificationRepository.save(commonNoti);

        // then
        var notifications1 = notificationRepository.findEssayNotificationByUserId(t1.getId());
        var notifications2 = notificationRepository.findEssayNotificationByUserId(s1.getId());
        EssayNotification notification1 = notifications1.get(0);
        EssayNotification notification2 = notifications2.get(0);

        assertAll(
                () -> assertThat(notifications1.size()).isEqualTo(1),
                () -> assertThat(notification1.getTitle()).isEqualTo("첨삭요청 알림"),
                () -> assertThat(notification1.getBody()).isEqualTo(sname + "님으로부터 첨삭요청이 들어왔습니다."),
                () -> assertThat(notifications2.size()).isEqualTo(1),
                () -> assertThat(notification2.getTitle()).isEqualTo("첨삭거절 알림"),
                () -> assertThat(notification2.getBody()).isEqualTo(tname + "님이 첨삭요청을 거절했습니다.")
        );
    }

    @Test
    @DisplayName("전체알림 전체조회 테스트")
    void findCommonNotificationByUserTest() {
        // given
        User s1 = DemoDataFactory.createStudent1(1L);
        User t1 = DemoDataFactory.createTeacher1(2L);
        Essay essay1 = DemoDataFactory.createEssay1(1L, s1, t1, EssayState.REQUEST, ReviewState.OFF);

        String name = s1.getName();
        EssayNotification essayNoti = EssayNotification.builder()
                .title("첨삭요청 알림")
                .body(name + "님으로부터 첨삭요청이 들어왔습니다.")
                .user(t1) // 알림을 받는 유저
                .essay(essay1)
                .build();

        CommonNotification commonNoti = CommonNotification.builder()
                .title("전체공지")
                .body("서버 점검이 예정되어 있습니다.")
                .build();

        CommonNotification commonNoti2 = CommonNotification.builder()
                .title("전체공지")
                .body("이벤트를 진행합니다.")
                .build();

        // when
        userRepository.save(s1);
        userRepository.save(t1);
        essayRepository.save(essay1);
        notificationRepository.save(essayNoti);
        notificationRepository.save(commonNoti);
        notificationRepository.save(commonNoti2);

        // then
        List<CommonNotification> notifications = notificationRepository.findAllCommonNotification();
        assertThat(notifications.size()).isEqualTo(2);
    }
}