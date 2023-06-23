package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import lombok.Getter;

@Getter
public class ChangeEssayStateResponse {

    private final String message;
    private final EssayResponse essay;

    public ChangeEssayStateResponse(String message, Essay essay) {
        this.message = message;
        this.essay = new EssayResponse(essay);
    }
}