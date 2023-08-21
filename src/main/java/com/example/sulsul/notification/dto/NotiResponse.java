package com.example.sulsul.notification.dto;

import com.example.sulsul.notification.entity.NotiType;
import com.example.sulsul.notification.entity.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Schema(description = "단일 알림 데이터")
public class NotiResponse {

    @Schema(description = "알림 id", example = "1")
    private final Long id;

    @Schema(description = "알림 제목", example = "공지사항")
    private final String title;

    @Schema(description = "알림 내용", example = "서버 점검이 예정되어 있습니다.")
    private final String body;

    @Schema(description = "알림 타입", example = "COMMON")
    private final String notiType;

//    @Schema(description = "알림 생성일", example = "2023-08-22")
//    private final String createdDate;

    @Schema(description = "알림 생성후 경과시간", example = "1일전")
    private final String createdAt;

    @Schema(description = "관련 첨삭 id, 전체 알림의 경우 -1로 응답", example = "-1")
    private final Long essayId;

    public NotiResponse(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.body = notification.getBody();
        this.notiType = notification.getNotiType().name();
//        this.createdDate = notification.getCreatedDate().toString();

        if (notiType.equals(NotiType.ESSAY)) {
            this.essayId = notification.getEssay().getId();
        } else {
            this.essayId = -1L;
        }

        this.createdAt = calculateElapsedTime(notification);
    }

    private String calculateElapsedTime(Notification notification) {
        LocalDateTime start = notification.getCreatedDate();
        LocalDateTime end = LocalDateTime.now();
        long between = ChronoUnit.DAYS.between(start, end);
        if (between == 0) {
            return "오늘";
        }
        return between + "일전";
    }
}