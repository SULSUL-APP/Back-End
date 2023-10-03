package com.example.sulsul.comment.entity;

import com.example.sulsul.common.BaseEntity;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "comments")
@Builder
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essay_id")
    private Essay essay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 1000, nullable = false)
    private String detail;

    public void updateDetail(String detail) {
        this.detail = detail;
    }
}