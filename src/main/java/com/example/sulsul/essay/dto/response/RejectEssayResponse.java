package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import lombok.Getter;

@Getter
public class RejectEssayResponse extends EssayResponse {

    private final String inquiry;
    private final String studentFilePath;
    private final String rejectDetail;

    public RejectEssayResponse(Essay essay, String studentFilePath) {
        super(essay);
        this.inquiry = essay.getInquiry();
        this.studentFilePath = studentFilePath;
        this.rejectDetail = essay.getRejectDetail();
    }
}