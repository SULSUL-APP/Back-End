package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.user.dto.StudentResponse;
import com.example.sulsul.user.dto.TeacherResponse;
import lombok.Getter;

@Getter
public class CreateEssayResponse {

    private final Long essayId;
    private final TeacherResponse teacher;
    private final StudentResponse student;

    public CreateEssayResponse(Essay essay) {
        this.essayId = essay.getId();
        this.teacher = new TeacherResponse(essay.getTeacher());
        this.student = new StudentResponse(essay.getStudent());
    }
}