package com.example.sulsul.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.sulsul.exception.file.EmptyFileException;
import com.example.sulsul.exception.file.s3.S3DeleteException;
import com.example.sulsul.exception.file.s3.S3UploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FileS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${s3.host.name}")
    private String hostName;

    private final AmazonS3 amazonS3;
    private final AmazonS3Client amazonS3Client;


    /**
     * PDF 파일을 S3 스토리지에 업로드한다.
     * @param multipartFile 업로드할 PDF 파일
     * @param dirName S3 내부 디렉토리 이름
     * @return 업로드 파일 S3 내부 URL
     */
    public String uploadFile(MultipartFile multipartFile, String dirName) {

        if (multipartFile.isEmpty()) {
            throw new EmptyFileException();
        }

        String s3FileName = dirName + "/" + UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        ObjectMetadata obj = new ObjectMetadata();
        obj.setContentLength(multipartFile.getSize());
        obj.setContentType(multipartFile.getContentType());

        try {
            amazonS3.putObject(bucketName, s3FileName, multipartFile.getInputStream(), obj);
        } catch (Exception e) {
            log.error("버킷 파일 업로드에 실패하였습니다.");
            throw new S3UploadException(bucketName);
        }

        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }


    /**
     * S3 스토리지 내부의 파일을 삭제한다.
     * @param fileUrl 삭제할 파일 URL
     */
    public void deleteFile(String fileUrl) {

        if (!fileUrl.startsWith(hostName)) {
            throw new S3DeleteException(bucketName, fileUrl);
        }

        String decodeURL = decodeURL(fileUrl);
        boolean isObjectExist = amazonS3Client.doesObjectExist(bucketName, decodeURL);
        log.info("Delete fileUrl={}", decodeURL);

        if (isObjectExist) {
            amazonS3Client.deleteObject(bucketName, decodeURL);
        } else {
            log.error("버킷 내부에 존재하지 않는 파일입니다. 삭제가 불가능합니다.");
            throw new S3DeleteException(bucketName, decodeURL);
        }
    }


    /**
     *
     * @param dirName
     * @param fileName
     * @return
     */
    public String getFile(String dirName, String fileName) {
        String filePath = dirName + '/' + fileName;
        boolean isObjectExist = amazonS3.doesObjectExist(bucketName, filePath);

        if(isObjectExist) {
            log.info("Get fileUrl={}", filePath);
            return amazonS3.getUrl(bucketName, filePath).toString();
        } else {
            return null;
            //추후 NotFound Exception 으로 수정 예정
        }
    }


    /**
     * 파일 경로 URL 을 utf-8 디코딩하여 반환한다.
     * @param fileUrl 파일 경로
     * @return 디코딩 URL
     */
    public String decodeURL(String fileUrl) {
        return URLDecoder.decode(
                fileUrl.replace(hostName, "").replaceAll("\\p{Z}", ""),
                StandardCharsets.UTF_8
        );
    }


}
