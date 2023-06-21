package com.example.sulsul.essay.entity;

import com.example.sulsul.common.BaseEntity;
import com.example.sulsul.common.type.EType;
import com.example.sulsul.essay.entity.type.EssayState;
import com.example.sulsul.essay.entity.type.ReviewState;
import com.example.sulsul.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Essay extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "essay_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

//    @OneToMany(mappedBy = "essay", cascade = CascadeType.ALL)
//    private List<Comment> commentList = new ArrayList<>();
//
//    @OneToOne(mappedBy = "essay", cascade = CascadeType.ALL)
//    private Review review
//
//    @OneToMany(mappedBy = "essay", cascade = CascadeType.ALL)
//    private List<File> fileList = new ArrayList<>();

    @Column(nullable = false)
    @NotBlank(message = "대학 이름은 필수 값입니다.")
    private String univ;

    @Column(nullable = false)
    @NotBlank(message = "논술 연도는 필수 값입니다.")
    private String year;

    @Column(nullable = false)
    private String inquiry; // 문의사항

    @Column()
    private String rejectDetail; //거절 사유

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EType eType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EssayState essayState; // 첨삭 상태

    @Enumerated(EnumType.STRING)
    private ReviewState reviewState; // 리뷰 생성 여부



    public void updateEType(EType eType) {
        this.eType = eType;
    }

    public void updateEssayState(EssayState essayState) {
        this.essayState = essayState;
    }

    public void updateReviewState(ReviewState reviewState) {
        this.reviewState = reviewState;
    }

    public void updateRejectDetail(String rejectDetail) {
        this.rejectDetail = rejectDetail;
    }

}