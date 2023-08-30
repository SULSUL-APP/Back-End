package com.example.sulsul.config.jwt.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokenDto {

    private String accessToken;
    private String refreshToken;

}
