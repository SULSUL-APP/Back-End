package com.example.sulsul.review.entity;

import com.example.sulsul.common.BaseEntity;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "reviews")
@Builder
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(length = 1000, nullable = false)
    private String detail;

    @Column(nullable = false)
    private int score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essay_id")
    private Essay essay;

    public void updateDetail(String detail) {
        this.detail = detail;
    }

    public void updateScore(int score) {
        this.score = score;
    }

}