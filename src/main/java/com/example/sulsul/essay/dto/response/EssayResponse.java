package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.user.entity.User;
import lombok.Getter;

@Getter
public class EssayResponse {

    private Long id;
    private String univ;
    private String year;
    private String eType;

    // TODO: User -> UserDto
    private User student;
    private User teacher;

    public EssayResponse(Essay essay) {
        this.id = essay.getId();
        this.univ = essay.getUniv();
        this.year = essay.getYear();
        this.eType = essay.getEType();
        this.teacher = essay.getTeacher();
        this.student = essay.getStudent();
    }
}