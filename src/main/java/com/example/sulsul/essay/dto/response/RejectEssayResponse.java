package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class RejectEssayResponse extends EssayResponse {

    @Schema(description = "문의사항", example = "첨삭 잘 부탁드립니다.")
    private final String inquiry;

    @Schema(description = "학생 첨삭파일 경로",
            example = "https://sulsul.s3.ap-northeast-2.amazonaws.com/files/314a32f7_sulsul.pdf")
    private final String studentFilePath;

    @Schema(description = "거절 사유", example = "거절 사유 예시")
    private final String rejectDetail;

    public RejectEssayResponse(Essay essay, String studentFilePath) {
        super(essay);
        this.inquiry = essay.getInquiry();
        this.studentFilePath = studentFilePath;
        this.rejectDetail = essay.getRejectDetail();
    }
}