package com.example.sulsul.exception;

import java.util.Map;

public class AccessNotAllowedException extends BaseException {
    public AccessNotAllowedException(String code, String message) {
        super(code, message);
    }

    public AccessNotAllowedException(String code, String message, Map<String, String> errors) {
        super(code, message, errors);
    }
}