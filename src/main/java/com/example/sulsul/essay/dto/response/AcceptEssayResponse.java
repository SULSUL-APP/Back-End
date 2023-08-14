package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import lombok.Getter;

@Getter
public class AcceptEssayResponse extends ChangeEssayStateResponse {
    public AcceptEssayResponse(Essay essay) {
        super("첨삭요청이 수락되었습니다.", essay);
    }
}