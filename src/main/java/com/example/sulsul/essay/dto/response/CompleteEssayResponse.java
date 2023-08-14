package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import lombok.Getter;

@Getter
public class CompleteEssayResponse extends ChangeEssayStateResponse {
    public CompleteEssayResponse(Essay essay) {
        super("첨삭이 완료되었습니다.", essay);
    }
}