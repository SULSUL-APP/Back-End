package com.example.sulsul.exception.file.s3;

import com.example.sulsul.exception.file.FileErrorCode;

public class S3DeleteException extends S3FileException {
    public S3DeleteException(String bucketName, String fileName) {
        super(FileErrorCode.S3_FILE_DELETE, bucketName);
        this.getErrors().put("fileName", fileName);
    }
}