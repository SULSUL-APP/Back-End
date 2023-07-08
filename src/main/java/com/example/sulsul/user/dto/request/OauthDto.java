package com.example.sulsul.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class OauthDto {

    @Schema(example = "엑세스 토큰")
    private String accessToken;

    @Schema(example = "구글, 애플 전용")
    private String idToken;

    @Schema(description = "소셜 로그인 origin (전부 소문자)", example = "kakao")
    private String origin;

}
