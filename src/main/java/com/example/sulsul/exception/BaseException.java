package com.example.sulsul.exception;

import java.util.HashMap;
import java.util.Map;

public class BaseException extends RuntimeException {
    private String code;
    private Map<String, String> errors = new HashMap<>();

    public BaseException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(String code, String message, Map<String, String> errors) {
        super(message);
        this.code = code;
        this.errors = errors;
    }

    public String getCode() {
        return code;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}