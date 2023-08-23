package com.example.sulsul.exception.jwt;

import com.example.sulsul.exception.AccessNotAllowedException;

public class AccessDenied extends AccessNotAllowedException {
    public AccessDenied() {
        super("JWT_02", "접근이 금지되었습니다.");
    }
}
