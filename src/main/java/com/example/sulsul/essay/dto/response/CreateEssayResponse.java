package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import lombok.Getter;

@Getter
public class CreateEssayResponse {

    private final Long essayId;

    // 강사와 학생의 이름, 프로필 이미지를 포함해야 함
    // UserDetailDTO에 data를 담아 응답하도록 해야 함
    // 일단은 강사와 학생의 id만 담아 응답하도록 함
    private final Long teacherId;
    private final Long studentId;

    public CreateEssayResponse(Essay essay) {
        this.essayId = essay.getId();
        this.teacherId = essay.getTeacher().getId();
        this.studentId = essay.getStudent().getId();
    }
}