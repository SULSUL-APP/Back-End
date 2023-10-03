package com.example.sulsul.config.jwt.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenDto {

    private String accessToken;
    private String refreshToken;
    private String isGuest;
}