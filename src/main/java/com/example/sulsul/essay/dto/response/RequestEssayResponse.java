package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import lombok.Getter;

@Getter
public class RequestEssayResponse extends EssayResponse {

    private String inquiry;
    private String studentFilePath;

    public RequestEssayResponse(Essay essay, String studentFilePath) {
        super(essay);
        this.inquiry = essay.getInquiry();
        this.studentFilePath = studentFilePath;
    }
}