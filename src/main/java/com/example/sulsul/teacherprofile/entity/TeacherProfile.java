package com.example.sulsul.teacherprofile.entity;

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
public class TeacherProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_profile_id")
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User teacher;

    @Column()
    private String careerDetail;

    @Column()
    private String introDetail;

    @Column()
    private String price;

    @Column()
    private String possibleUniv;

    @Column()
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

}