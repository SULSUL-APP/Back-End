package com.example.sulsul.notification.entity;

import java.util.function.Function;

public enum NotiBody {

    REQUEST(name -> name + "님이 첨삭을 요청하였습니다."),
    ACCEPT(name -> name + "님이 첨삭요청을 수락하였습니다."),
    REJECT(name -> name + "님이 첨삭요청을 거절하였습니다."),
    COMPLETE(name -> name + "님이 첨삭을 완료처리하였습니다."),
    COMMENT(name -> name + "님이 댓글을 작성하였습니다."),
    FILE(name -> name + "님이 파일을 업로드하였습니다.");


    private final Function<String, String> converter;

    NotiBody(Function<String, String> converter) {
        this.converter = converter;
    }

    public String getDetail(String name) {
        return converter.apply(name);
    }
}