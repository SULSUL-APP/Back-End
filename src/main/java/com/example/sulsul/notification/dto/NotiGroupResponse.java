package com.example.sulsul.notification.dto;

import com.example.sulsul.notification.entity.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class NotiGroupResponse {

    @Schema(description = "알림 리스트")
    private final List<NotiResponse> notifications = new ArrayList<>();

    public NotiGroupResponse(List<Notification> notifications) {
        notifications.stream()
                .map(NotiResponse::new)
                .forEach(noti -> this.notifications.add(noti));
    }
}