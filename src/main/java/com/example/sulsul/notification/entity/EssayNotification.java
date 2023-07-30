package com.example.sulsul.notification.entity;

import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@DiscriminatorValue("ESSAY")
@Setter(AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EssayNotification extends Notification {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essay_id")
    private Essay essay;

    @Builder
    public EssayNotification(String title, String body, User user, Essay essay) {
        super(title, body);
        this.user = user;
        this.essay = essay;
    }
}