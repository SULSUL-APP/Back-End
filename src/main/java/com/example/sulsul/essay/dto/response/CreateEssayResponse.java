package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.user.entity.User;
import lombok.Getter;

@Getter
public class CreateEssayResponse {

    private final Long essayId;

    // User -> UserDto
    private final User teacher;
    private final User student;

    public CreateEssayResponse(Essay essay) {
        this.essayId = essay.getId();
        this.teacher = essay.getTeacher();
        this.student = essay.getStudent();
    }
}