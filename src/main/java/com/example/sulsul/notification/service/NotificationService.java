package com.example.sulsul.notification.service;

import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.notification.entity.Notification;
import com.example.sulsul.notification.repository.NotificationRepository;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 첨삭알림 생성
     *
     * @param title  알림 제목
     * @param body   알림 내용
     * @param target 알림 수신자
     * @param essay  알림 관련 essay
     */
    @Transactional
    public void saveEssayNotification(String title, String body, User target, Essay essay) {
        Notification notification = Notification.builder()
                .title(title)
                .body(body)
                .user(target)
                .essay(essay)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 전체알림 생성
     *
     * @param title 알림 제목
     * @param body  알림 내용
     */
    @Transactional
    public void saveCommonNotification(String title, String body) {
        Notification notification = new Notification(title, body);
        notificationRepository.save(notification);
    }

    /**
     * 알림 일괄삭제
     *
     * @param ids 삭제할 알림 id 리스트
     */
    @Transactional
    public void deleteNotifications(List<Long> ids) {
        notificationRepository.deleteNotifications(ids);
    }

    /**
     * 유저별 알림 조회
     *
     * @param userId 알림을 조회할 유저 id
     * @return 알림 리스트
     */
    @Transactional(readOnly = true)
    public List<Notification> getEssayNotifications(Long userId) {
        return notificationRepository.findNotificationByUserId(userId);
    }
}