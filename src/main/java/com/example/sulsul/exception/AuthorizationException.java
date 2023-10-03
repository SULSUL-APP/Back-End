package com.example.sulsul.exception;

import java.util.Map;

public class AuthorizationException extends BaseException {
    public AuthorizationException(String code, String message) {
        super(code, message);
    }

    public AuthorizationException(String code, String message, Map<String, String> errors) {
        super(code, message, errors);
    }
}