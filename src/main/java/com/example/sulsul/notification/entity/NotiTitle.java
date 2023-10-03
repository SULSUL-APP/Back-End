package com.example.sulsul.notification.entity;

public enum NotiTitle {

    REQUEST("첨삭요청 알림"),
    COMMENT("첨삭에 댓글이 작성되었습니다."),
    ACCEPT("첨삭요청이 수락되었습니다."),
    REJECT("첨삭요청이 거절되었습니다."),
    COMPLETE("첨삭이 완료되었습니다."),
    FILE("첨삭에 파일이 업로드되었습니다.");

    private final String title;

    NotiTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}