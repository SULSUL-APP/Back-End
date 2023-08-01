package com.example.sulsul.notification.service;

import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.notification.entity.CommonNotification;
import com.example.sulsul.notification.entity.EssayNotification;
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

    // 첨삭알림 생성 - target 고려
    @Transactional
    public void saveEssayNotification(String title, String body, User target, Essay essay) {
        EssayNotification notification = EssayNotification.builder()
                .title(title)
                .body(body)
                .user(target)
                .essay(essay)
                .build();

        notificationRepository.save(notification);
    }

    // 전체알림 생성
    @Transactional
    public void saveCommonNotification(String title, String body) {
        CommonNotification notification = CommonNotification.builder()
                .title(title)
                .body(body)
                .build();

        notificationRepository.save(notification);
    }

    // 알림 삭제
    @Transactional
    public void deleteNotifications(List<Long> ids) {
        notificationRepository.deleteNotifications(ids);
    }

    // 전체알림 전체조회
    @Transactional(readOnly = true)
    public List<CommonNotification> getCommonNotifications() {
        return notificationRepository.findAllCommonNotification();
    }

    /// 첨삭알림 유저별 조회
    @Transactional(readOnly = true)
    public List<EssayNotification> getEssayNotifications(Long userId) {
        return notificationRepository.findEssayNotificationByUserId(userId);
    }

}