package com.example.sulsul.exception;

import java.util.Map;

public class S3FileException extends BaseException {
    public S3FileException(String code, String message) {
        super(code, message);
    }

    public S3FileException(String code, String message, Map<String, String> errors) {
        super(code, message, errors);
    }
}