package com.example.sulsul.exception.fcm;

import com.example.sulsul.exception.FcmMessageException;

import java.util.Map;

public class CommonMessageFailureException extends FcmMessageException {
    public CommonMessageFailureException(String title, String body) {
        super("FCM_03", "전체알림 전송에 실패하였습니다.", Map.of("title", title, "body", body));
    }
}