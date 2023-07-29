package com.example.sulsul.exception.fcm;

import com.example.sulsul.exception.FcmMessageException;

import java.util.Map;

public class EssayMessageFailureException extends FcmMessageException {
    public EssayMessageFailureException(long targetId, String title, String body) {
        super("FCM_02", "첨삭알림 전송에 실패하였습니다.",
                Map.of("targetId", String.valueOf(targetId), "title", title, "body", body));
    }
}