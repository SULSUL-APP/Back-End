package com.example.sulsul.teacherprofile.dto.response;

import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import com.example.sulsul.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

public class ProfileResponse {

    @Schema(description = "강사 이름", example = "임탁균")
    private final String name;

    @Schema(description = "강사 프로필 이미지", example = "이미지 path")
    private final String url;

    @Schema(description = "평점", example = "리뷰를 통한 평점")
    private final Double reviewScore;

    public ProfileResponse(TeacherProfile teacherProfile) {
        this.name = teacherProfile.getTeacher().getName();
        this.url = teacherProfile.getTeacher().getProfileImage();
        this.reviewScore = teacherProfile.getReviewScore();
    }
}
