package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import lombok.Getter;

@Getter
public class ChangeEssayStateResponse {

    private String message;
    private EssayResponse essay;

    public ChangeEssayStateResponse(String message, Essay essay) {
        this.message = message;
        this.essay = new EssayResponse(essay);
    }
}