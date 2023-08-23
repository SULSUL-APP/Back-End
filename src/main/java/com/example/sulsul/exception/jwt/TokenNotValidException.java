package com.example.sulsul.exception.jwt;

import com.example.sulsul.exception.AccessNotAllowedException;

public class TokenNotValidException extends AccessNotAllowedException {
    public TokenNotValidException() {
        super("JWT_01", "토큰 유효성 검사에 실패하였습니다.");
    }
}
