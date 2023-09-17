package com.example.sulsul.exception.auth;

import com.example.sulsul.exception.AuthenticationException;

public class NotAuthenticatedException extends AuthenticationException {
    public NotAuthenticatedException() {
        super("AUTH_01", "인증되지 않은 유저입니다.");
    }
}