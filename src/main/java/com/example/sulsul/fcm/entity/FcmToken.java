package com.example.sulsul.fcm.entity;

import com.example.sulsul.common.BaseEntity;
import com.example.sulsul.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Table(name = "fcm_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fcm_token_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(message = "Fcm 토큰 값은 필수입니다.")
    @Column(length = 1000, nullable = false)
    private String fcmToken;

    public FcmToken(User user, String fcmToken) {
        this.user = user;
        this.fcmToken = fcmToken;
    }

    public FcmToken(Long id, User user, String fcmToken) {
        this.id = id;
        this.user = user;
        this.fcmToken = fcmToken;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}