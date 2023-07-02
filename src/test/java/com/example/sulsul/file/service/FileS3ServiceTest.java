package com.example.sulsul.file.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FileS3ServiceTest {


    @Autowired
    FileS3Service fileS3Service;

    @DisplayName("S3 파일 업로드 테스트")
    @Test
    void S3FileUploadTest() throws Exception {
        //given

        //when

        //then

    }


    @DisplayName("S3 파일 삭제 테스트")
    @Test
    void S3FileDeleteTest() throws Exception {
        //given

        //when

        //then
        fileS3Service.deleteFile("https://sulsul-bucket.s3.ap-northeast-2.amazonaws.com/%EA%B8%B0%EB%A7%90+%EB%B0%9C%ED%91%9C_sulsul_%EC%B5%9C%EC%A2%85.pdf");
    }


}