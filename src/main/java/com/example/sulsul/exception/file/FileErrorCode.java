package com.example.sulsul.exception.file;

import com.example.sulsul.exception.BaseErrorCode;

public enum FileErrorCode implements BaseErrorCode {

    EMPTY_FILE("FILE_O1", "비어있는 파일."),
    S3_FILE_DELETE("FILE_02", "버킷에 존재하지 않는 파일."),
    S3_FILE_UPLOAD("FILE_03", "버킷 파일 업로드 오류 발생.");


    private String code;
    private String message;

    FileErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return null;
    }
}
