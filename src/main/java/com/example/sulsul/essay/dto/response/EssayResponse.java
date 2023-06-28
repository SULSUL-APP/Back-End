package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.user.dto.StudentResponse;
import com.example.sulsul.user.dto.TeacherResponse;
import lombok.Getter;

@Getter
public class EssayResponse {

    private final Long id;
    private final String univ;
    private final String examYear;
    private final String eType;
    private final EssayState essayState;
    private final StudentResponse student;
    private final TeacherResponse teacher;

    public EssayResponse(Essay essay) {
        this.id = essay.getId();
        this.univ = essay.getUniv();
        this.examYear = essay.getExamYear();
        this.eType = essay.getEType();
        this.essayState = essay.getEssayState();
        this.teacher = new TeacherResponse(essay.getTeacher());
        this.student = new StudentResponse(essay.getStudent());
    }
}