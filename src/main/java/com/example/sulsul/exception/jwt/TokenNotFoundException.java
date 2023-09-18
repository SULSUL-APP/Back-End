package com.example.sulsul.exception.jwt;

import com.example.sulsul.exception.AccessNotAllowedException;

public class TokenNotFoundException extends AccessNotAllowedException {
    public TokenNotFoundException() {
        super("JWT_04", "AccessToken을 찾을 수 없습니다.");
    }
}