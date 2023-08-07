package com.example.sulsul.exception.s3;

import com.example.sulsul.exception.S3FileException;

import java.util.Map;

public class S3DeleteException extends S3FileException {
    public S3DeleteException(String bucketName, String filePath) {
        super("S3_02", "S3 버킷에 삭제할 파일이 존재하지 않습니다.", Map.of("bucketName", bucketName, "filePath", filePath));
    }
}