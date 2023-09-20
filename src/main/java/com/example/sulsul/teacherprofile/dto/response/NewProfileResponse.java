package com.example.sulsul.teacherprofile.dto.response;

import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class NewProfileResponse {
    @Schema(description = "신규 강사 Id", example = "1")
    private final Long id;

    @Schema(description = "신규 강사 이름", example = "임탁균")
    private final String name;

    @Schema(description = "신규 강사 프로필 이미지", example = "이미지 path")
    private final String url;

    @Schema(description = "신규 강사 평점", example = "리뷰를 통한 평점")
    private final String reviewScore;

    public NewProfileResponse(TeacherProfile teacherProfile) {
        this.id = teacherProfile.getId();
        this.name = teacherProfile.getTeacher().getName();
        this.url = teacherProfile.getTeacher().getProfileImage();
        this.reviewScore = teacherProfile.getReviewScore();
    }
}
