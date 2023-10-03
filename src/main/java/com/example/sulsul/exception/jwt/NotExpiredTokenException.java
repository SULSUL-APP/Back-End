package com.example.sulsul.exception.jwt;

import com.example.sulsul.exception.AuthenticationException;

public class NotExpiredTokenException extends AuthenticationException {
    public NotExpiredTokenException() {
        super("JWT_05", "만료된 AccessToken이 아닙니다.");
    }
}