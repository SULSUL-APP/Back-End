package com.example.sulsul.notification.entity;

import com.example.sulsul.common.BaseEntity;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(length = 1000, nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essay_id")
    private Essay essay;

    @Enumerated(EnumType.STRING)
    private NotiType notiType;

    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
        this.notiType = NotiType.COMMON;
    }

    @Builder
    public Notification(String title, String body, User user, Essay essay) {
        this.title = title;
        this.body = body;
        this.notiType = NotiType.ESSAY;
        this.user = user;
        this.essay = essay;
    }
}