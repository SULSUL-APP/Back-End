package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.entity.type.EssayState;
import com.example.sulsul.user.entity.User;
import lombok.Getter;

@Getter
public class EssayResponse {

    private final Long id;
    private final String univ;
    private final String examYear;
    private final String eType;
    private final EssayState essayState;

    // TODO: User -> UserDto
    private final User student;
    private final User teacher;

    public EssayResponse(Essay essay) {
        this.id = essay.getId();
        this.univ = essay.getUniv();
        this.examYear = essay.getExamYear();
        this.eType = essay.getEType();
        this.essayState = essay.getEssayState();
        this.teacher = essay.getTeacher();
        this.student = essay.getStudent();
    }
}