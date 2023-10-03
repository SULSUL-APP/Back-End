package com.example.sulsul.exception.refresh;

import com.example.sulsul.exception.AuthorizationException;

import java.util.Map;

public class RefreshTokenExpiredException extends AuthorizationException {
    public RefreshTokenExpiredException() {
        super("REFRESH_01", "RefreshToken의 유효기간이 지났습니다. 강제 로그아웃이 필요합니다.",
                Map.of("API_CALL", "GET /auth/logout"));
    }
}