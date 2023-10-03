package com.example.sulsul.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class KakaoTokenRequest {

    @Schema(description = "카카오 access_token")
    @NotBlank(message = "카카오 유저 정보를 가져오기 위한 access_token")
    private final String kakaoToken;

    @Schema(description = "FCM token")
    @NotBlank(message = "알림 기능을 위한 FCM 토큰값 설정")
    private final String fcmToken;

}