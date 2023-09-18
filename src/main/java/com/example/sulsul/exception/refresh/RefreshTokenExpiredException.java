package com.example.sulsul.exception.refresh;

import com.example.sulsul.exception.AuthorizationException;

public class RefreshTokenExpiredException extends AuthorizationException {
    public RefreshTokenExpiredException() {
        super("REFRESH_01", "RefreshToken의 유효기간이 지났습니다.");
    }
}