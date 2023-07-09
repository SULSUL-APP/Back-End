package com.example.sulsul.exception.notFound;

import com.example.sulsul.exception.BaseErrorCode;

public enum ResourceNotFoundErrorCode implements BaseErrorCode {

    USER_NOT_FOUND("USER_00", "존재하지 않는 유저입니다."),
    UTYPE_NOT_FOUND("UTYPE_00", "존재하지 않는 유저 타입입니다."),
    ETYPE_NOT_FOUND("ETYPE_00", "존재하지 않는 분야입니다.");

    private String code;
    private String message;

    ResourceNotFoundErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
