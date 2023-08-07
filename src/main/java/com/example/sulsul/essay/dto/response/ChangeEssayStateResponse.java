package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ChangeEssayStateResponse {

    @Schema(description = "상태 변경 메세지", example = "첨삭이 완료되었습니다.")
    private final String message;

    private final EssayResponse essay;

    public ChangeEssayStateResponse(String message, Essay essay) {
        this.message = message;
        this.essay = new EssayResponse(essay);
    }
}