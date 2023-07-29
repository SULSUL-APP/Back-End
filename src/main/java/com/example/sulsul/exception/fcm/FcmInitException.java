package com.example.sulsul.exception.fcm;

import com.example.sulsul.exception.FcmMessageException;

public class FcmInitException extends FcmMessageException {
    public FcmInitException() {
        super("FCM_01", "FCM Initialize 과정 중 오류발생");
    }
}