package com.example.sulsul.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private String email;
    private String name;
    private String accessToken;
    private String refreshToken;
    private boolean initData;
}
