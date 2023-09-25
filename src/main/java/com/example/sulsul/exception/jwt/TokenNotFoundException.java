package com.example.sulsul.exception.jwt;

import com.example.sulsul.exception.AuthenticationException;

public class TokenNotFoundException extends AuthenticationException {
    public TokenNotFoundException() {
        super("JWT_04", "AccessToken을 찾을 수 없습니다.");
    }
}