package com.example.sulsul.exception.file.s3;

import com.example.sulsul.exception.BaseErrorCode;
import com.example.sulsul.exception.file.FileErrorCode;

public class S3UploadException extends S3FileException {
    public S3UploadException(String bucketName) {
        super(FileErrorCode.S3_FILE_UPLOAD, bucketName);
    }
}
