package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;

public class RejectEssayResponse extends EssayResponse {
    private String inquiry;
    private String studentFilePath;
    private String rejectDetail;

    public RejectEssayResponse(Essay essay, String studentFilePath) {
        super(essay);
        this.inquiry = essay.getInquiry();
        this.studentFilePath = studentFilePath;
        this.rejectDetail = essay.getRejectDetail();
    }
}