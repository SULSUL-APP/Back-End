package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.user.dto.StudentResponse;
import com.example.sulsul.user.dto.TeacherResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class EssayResponse {

    @Schema(description = "첨삭 id", example = "1")
    private final Long id;

    @Schema(description = "대학명", example = "홍익대")
    private final String univ;

    @Schema(description = "시험년도", example = "2022")
    private final String examYear;

    @Schema(description = "논술분야", example = "수리")
    private final String eType;

    @Schema(description = "첨삭상태", example = "REQUEST",
            allowableValues = {"REQUEST", "REJECT", "PROCEED", "COMPLETE"})
    private final String essayState;

    private final StudentResponse student;
    private final TeacherResponse teacher;

    public EssayResponse(Essay essay) {
        this.id = essay.getId();
        this.univ = essay.getUniv();
        this.examYear = essay.getExamYear();
        this.eType = essay.getEType();
        this.essayState = essay.getEssayState().name();
        this.teacher = new TeacherResponse(essay.getTeacher());
        this.student = new StudentResponse(essay.getStudent());
    }
}