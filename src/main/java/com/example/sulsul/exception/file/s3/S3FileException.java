package com.example.sulsul.exception.file.s3;

import com.example.sulsul.exception.BaseErrorCode;
import com.example.sulsul.exception.file.FileException;

public class S3FileException extends FileException {
    public S3FileException(BaseErrorCode errorCode, String bucketName) {
        super(errorCode);
        super.getErrors().put("bucketName", bucketName);
    }
}
