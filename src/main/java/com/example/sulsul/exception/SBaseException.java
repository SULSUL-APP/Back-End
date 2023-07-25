package com.example.sulsul.exception;

public class SBaseException extends RuntimeException {

    private SBaseErrorCode errorCode;

    public SBaseErrorCode getErrorCode() {
        return errorCode;
    }

    public SBaseException(SBaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}