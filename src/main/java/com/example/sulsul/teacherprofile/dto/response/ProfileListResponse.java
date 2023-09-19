package com.example.sulsul.teacherprofile.dto.response;

import com.example.sulsul.review.dto.response.ReviewResponse;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProfileListResponse {
    @Schema(description = "강사 프로필 리스트")
    private final List<ProfileResponse> profiles = new ArrayList<>();

    public ProfileListResponse(List<TeacherProfile> profiles) {
        profiles.stream()
                .map(ProfileResponse::new)
                // Lambda => .forEach(teacherProfile -> this.profiles.add(teacherProfile));
                .forEach(this.profiles::add);
    }
}
