package com.example.sulsul.teacherprofile.entity;

import com.example.sulsul.common.BaseEntity;
import com.example.sulsul.teacherprofile.dto.request.TeacherProfileRequest;
import com.example.sulsul.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@Setter(AccessLevel.PROTECTED)
@Table(name = "teacher_profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TeacherProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_profile_id")
    private Long id;

    @JoinColumn(name = "user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User teacher;

    @Column(length = 1000)
    private String careerDetail;

    @Column(length = 1000)
    private String introDetail;

    @Column(length = 1000)
    private String price;

    @Column(length = 1000)
    private String possibleUniv;

    @Column(length = 1000)
    private String otherDetail; // 기타 사항

    @Column
    private String reviewScore;

    @Column
    private Integer completedCount;

    public boolean isNewTeacher() {
        return completedCount.equals(0);
    }

    public void addCompletedCount() {
        this.completedCount += 1;
    }

    public void updateReviewScore(String reviewScore) {
        this.reviewScore = reviewScore;
    }

    public TeacherProfile(User user, String reviewScore, Integer completedCount) {
        this.teacher = user;
        this.reviewScore = reviewScore;
        this.completedCount = completedCount;
    }

    public TeacherProfile updateTeacherProfile(TeacherProfileRequest teacherProfileRequest) {
        this.careerDetail = teacherProfileRequest.getCareerDetail();
        this.introDetail = teacherProfileRequest.getIntroDetail();
        this.price = teacherProfileRequest.getPrice();
        this.possibleUniv = teacherProfileRequest.getPossibleUniv();
        this.otherDetail = teacherProfileRequest.getOtherDetail();

        return this;
    }
}