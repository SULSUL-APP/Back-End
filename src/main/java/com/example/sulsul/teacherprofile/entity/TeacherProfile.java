package com.example.sulsul.teacherprofile.entity;

import com.example.sulsul.teacherprofile.dto.request.TeacherProfileRequest;
import com.example.sulsul.user.entity.User;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Entity
@Getter
@Builder
@Setter(AccessLevel.PROTECTED)
@Table(name = "teacher_profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TeacherProfile {

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

    @Column()
    private String price;

    @Column(length = 1000)
    private String possibleUniv;

    @Column(length = 1000)
    private String otherDetail; // 기타 사항

    @Column()
    private Double reviewScore;

    @Column()
    private Integer completedCount;


    public boolean isNewTeacher() {
        return completedCount.equals(0);
    }

    public void addCompletedCount() {
        this.completedCount += 1;
    }

    public void updateReviewScore(Double reviewScore) {
        this.reviewScore = reviewScore;
    }

    public void updateCareerDetail(String careerDetail) {
        this.careerDetail = careerDetail;
    }

    public void updateIntroDetail(String introDetail) {
        this.introDetail = introDetail;
    }

    public void updatePrice(String price) {
        this.price = price;
    }

    public void updatePossibleUniv(String possibleUniv) {
        this.possibleUniv = possibleUniv;
    }

    public void updateOtherDetail(String otherDetail) {
        this.otherDetail = otherDetail;
    }


    public TeacherProfile(User user, Double reviewScore, Integer completedCount) {
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