package com.example.sulsul.exception.file.s3;

import com.example.sulsul.exception.SBaseErrorCode;
import com.example.sulsul.exception.file.FileException;

public class S3FileException extends FileException {
    public S3FileException(SBaseErrorCode errorCode, String bucketName) {
        super(errorCode);
        super.getErrors().put("bucketName", bucketName);
    }
}