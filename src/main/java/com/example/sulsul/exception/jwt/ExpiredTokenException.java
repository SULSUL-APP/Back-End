package com.example.sulsul.exception.jwt;

import com.example.sulsul.exception.AccessNotAllowedException;

public class ExpiredTokenException extends AccessNotAllowedException {
    public ExpiredTokenException() {
        super("JWT_03", "AccessToken의 유효기간이 지났습니다. 토큰을 재발급 받아주세요.");
    }
}