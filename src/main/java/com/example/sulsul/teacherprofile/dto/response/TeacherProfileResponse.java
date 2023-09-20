package com.example.sulsul.teacherprofile.dto.response;

import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import com.example.sulsul.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class TeacherProfileResponse {

    //user
    @Schema(description = "강사 이름", example = "임탁균")
    private final String name;

    @Schema(description = "강사 프로필 이미지", example = "이미지 path")
    private final String url;

    @Schema(description = "강사 대표 문구", example = "논술은 언제나 류동완")
    private final String catchPhrase;

    //profile
    @Schema(description = "프로필 id", example = "1")
    private final Long id;

    @Schema(description = "강사 약력 및 학력", example = "강사 프로필 약력 및 학력 예시")
    private final String careerDetail;

    @Schema(description = "강사 소개 문구", example = "강사 프로필 소개 문구 예시")
    private final String introDetail;

    @Schema(description = "첨삭 가격", example = "50000")
    public final String price;

    @Schema(description = "가능 대학", example = "첨삭 가능 대학 예시")
    private final String possibleUniv;

    @Schema(description = "기타 사항", example = "기타 사항 문구 예시")
    private final String otherDetail;

    @Schema(description = "평점", example = "리뷰를 통한 평점")
    private final String reviewScore;

    @Schema(description = "완료한 첨삭 개수", example = "강사가 진행한 총 첨삭 개수")
    private final Integer completedCount;

    public TeacherProfileResponse(TeacherProfile teacherProfile) {
        this.name = teacherProfile.getTeacher().getName();
        this.url = teacherProfile.getTeacher().getProfileImage();
        this.catchPhrase = teacherProfile.getTeacher().getCatchPhrase();
        this.id = teacherProfile.getId();
        this.careerDetail = teacherProfile.getCareerDetail();
        this.introDetail = teacherProfile.getIntroDetail();
        this.price = teacherProfile.getPrice();
        this.possibleUniv = teacherProfile.getPossibleUniv();
        this.otherDetail = teacherProfile.getOtherDetail();
        this.reviewScore = teacherProfile.getReviewScore();
        this.completedCount = teacherProfile.getCompletedCount();
    }
}
