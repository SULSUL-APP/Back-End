package com.example.sulsul.common.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EssayState {
    REQUEST("첨삭요청"),
    PROCEED("첨삭진행"),
    REJECT("첨삭거절"),
    COMPLETE("첨삭완료");

    private final String state;

    EssayState(String state) {
        this.state = state;
    }

    @JsonValue // Json으로 변환할 때 Enum의 값을 어떻게 표현할지 결정
    public String getState() {
        return this.state;
    }
}