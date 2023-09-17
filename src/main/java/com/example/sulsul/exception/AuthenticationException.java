package com.example.sulsul.exception;

import java.util.Map;

public class AuthenticationException extends BaseException {
    public AuthenticationException(String code, String message) {
        super(code, message);
    }

    public AuthenticationException(String code, String message, Map<String, String> errors) {
        super(code, message, errors);
    }
}