package com.example.sulsul.exception.s3;

import com.example.sulsul.exception.S3FileException;

import java.util.Map;

public class S3ImageUploadException extends S3FileException {
    public S3ImageUploadException(String bucketName, String fileName) {
        super("S3_04", "S3 버킷에 이미지를 업로드하는데 실패했습니다.", Map.of("bucketName", bucketName, "fileName", fileName));
    }
}