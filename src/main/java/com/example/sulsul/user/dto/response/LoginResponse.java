package com.example.sulsul.user.dto.response;

import com.example.sulsul.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class LoginResponse {

    @Schema(description = "유저 아이디", example = "1")
    private final Long id;

    @Schema(description = "유저 이름", example = "김경근")
    private final String name;

    @Schema(description = "유저 이메일", example = "sulsul@gmail.com")
    private final String email;

    @Schema(description = "유저 프로필 이미지 저장경로",
            example = "https://sulsul.s3.ap-northeast-2.amazonaws.com/images/314a32f7_image.png")
    private final String profileImage;

    @Schema(description = "유저 타입", example = "STUDENT", allowableValues = {"STUDENT", "TEACHER"})
    private final String userType;

    @Schema(description = "첨삭 타입", example = "NATURE", allowableValues = {"NATURE", "SOCIAL"})
    private final String essayType;

    public LoginResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.profileImage = user.getProfileImage();
        this.userType = user.getUserType().name();
        this.essayType = user.getEssayType().name();
    }
}