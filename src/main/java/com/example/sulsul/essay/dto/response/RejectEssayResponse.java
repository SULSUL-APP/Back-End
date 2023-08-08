package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import lombok.Getter;

@Getter
public class RejectEssayResponse extends ChangeEssayStateResponse {
    public RejectEssayResponse(Essay essay) {
        super("첨삭요청이 거절되었습니다.", essay);
    }
}