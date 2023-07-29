package com.example.sulsul.fcm;

import com.example.sulsul.exception.fcm.CommonMessageFailureException;
import com.example.sulsul.exception.fcm.EssayMessageFailureException;
import com.example.sulsul.fcm.entity.FcmToken;
import com.example.sulsul.fcm.repository.FcmTokenRepository;
import com.example.sulsul.user.entity.User;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Async
@RequiredArgsConstructor
public class FcmMessageService {

    private final FcmTokenRepository fcmTokenRepository;

    /**
     * 첨삭알림 전송
     *
     * @param target 알림 대상
     * @param title  알림 제목
     * @param body   알림 내용
     */
    @Transactional(readOnly = true)
    public void sendToOne(User target, String title, String body) {

        Optional<FcmToken> token = fcmTokenRepository.findByUser(target);
        // 알림대상의 token이 존재하지 않는 경우
        if (token.isEmpty()) { // 로그아웃 상태
            return;
        }

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        String fcmToken = token.get().getFcmToken();
        Message message = Message.builder()
                .setNotification(notification)
                .setToken(fcmToken)
                .build();

        try {
            // 첨삭알림 전송
            FirebaseMessaging.getInstance().sendAsync(message);
        } catch (Exception e) {
            // 알림 전송 실패
            throw new EssayMessageFailureException(target.getId(), title, body);
        }
    }

    /**
     * 전체알림 전송
     * @param title 알림 제목
     * @param body 알림 내용
     */
    @Transactional(readOnly = true)
    public void sendToAll(String title, String body) {

        List<Message> messages = fcmTokenRepository.findAll()
                .stream()
                .map(token -> {
                    String fcmToken = token.getFcmToken();
                    Notification notification = Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build();

                    return Message.builder()
                            .setNotification(notification)
                            .setToken(fcmToken)
                            .build();
                }).collect(Collectors.toList());

        try {
            // 전체알림 전송
            FirebaseMessaging.getInstance().sendAll(messages);
        } catch (FirebaseMessagingException e) {
            // 알림 전송 실패
            throw new CommonMessageFailureException(title, body);
        }
    }
}