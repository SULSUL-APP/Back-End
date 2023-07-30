package com.example.sulsul.notification.entity;

import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("COMMON")
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonNotification extends Notification {
    @Builder
    public CommonNotification(String title, String body) {
        super(title, body);
    }
}