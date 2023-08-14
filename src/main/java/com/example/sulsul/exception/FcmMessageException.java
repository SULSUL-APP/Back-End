package com.example.sulsul.exception;

import java.util.Map;

public class FcmMessageException extends BaseException {
    public FcmMessageException(String code, String message) {
        super(code, message);
    }

    public FcmMessageException(String code, String message, Map<String, String> errors) {
        super(code, message, errors);
    }
}