package com.example.sulsul.exception.s3;

import com.example.sulsul.exception.S3FileException;

import java.util.Map;

public class S3FileNotFoundException extends S3FileException {
    public S3FileNotFoundException(String bucketName, String fileName) {
        super("S3_03", "S3 버킷에 해당 파일이 존재하지 않습니다.", Map.of("bucketName", bucketName, "fileName", fileName));
    }
}
