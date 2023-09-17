package com.example.sulsul.exception.refresh;

import com.example.sulsul.exception.AuthorizationException;

public class RefreshTokenVerificationException extends AuthorizationException {
    public RefreshTokenVerificationException() {
        super("REFRESH_04", "유저의 RefreshToken값이 일치하지 않습니다.");
    }
}