package com.example.sulsul.exception.jwt;

import com.example.sulsul.exception.AuthenticationException;

public class TokenNotValidException extends AuthenticationException {
    public TokenNotValidException() {
        super("JWT_01", "AccessToken 유효성 검사에 실패하였습니다.");
    }
}