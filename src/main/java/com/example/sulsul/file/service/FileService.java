package com.example.sulsul.file.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.exception.CustomException;
import com.example.sulsul.file.entity.File;
import com.example.sulsul.file.repository.FileRepository;
import com.example.sulsul.file.type.FileType;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${s3.host.name}")
    private String hostName;

    private final AmazonS3Client amazonS3;
    private final FileRepository fileRepository;

    /**
     * 파일의 확장자 추출
     *
     * @param file 확장자를 추출할 파일
     * @return 파일의 확장자 반환
     */
    private String getFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        int pos = filename.lastIndexOf(".");
        // 파일 확장자 반환
        return filename.substring(pos + 1);
    }

    /**
     * 첨삭파일을 S3 스토리지에 업로드하고 파일경로를 반환한다.
     *
     * @param uploader  첨삭파일을 업로드한 유저
     * @param essay     첨삭파일을 업로드한 에세이
     * @param essayFile s3 스토리지에 저장할 첨삭파일
     * @return 저장된 파일의 s3 스토리지 경로
     */
    @Transactional
    public File uploadEssayFile(User uploader, Essay essay, MultipartFile essayFile) {
        // 첨삭파일이 비어있는지 확인
        if (essayFile.isEmpty()) {
            throw new CustomException("첨삭파일이 비어있습니다.");
        }
        // 첨삭파일의 확장자가 pdf인지 확인
        if (!getFileExtension(essayFile).equals("pdf")) {
            throw new CustomException("첨삭파일은 pdf 파일만 업로드 가능합니다.");
        }
        // s3에 저장할 파일명 생성
        String s3FileName = "essays/" + UUID.randomUUID() + "_" + essayFile.getOriginalFilename();
        // s3에 저장할 파일의 메타데이터 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(essayFile.getSize());
        metadata.setContentType(essayFile.getContentType());
        // s3 스토리지에 첨삭파일 업로드
        try {
            amazonS3.putObject(bucketName, s3FileName, essayFile.getInputStream(), metadata);
        } catch (Exception e) {
            // 첨삭파일 업로드 에러 발생
            throw new CustomException(bucketName + "에 첨삭파일을 업로드하는데 실패했습니다.");
        }
        // 업로드한 첨삭파일의 s3 스토리지 경로
        String filePath = amazonS3.getUrl(bucketName, s3FileName).toString();
        // File 엔티티 생성
        return fileRepository.save(File.builder()
                .essay(essay)
                .user(uploader)
                .filePath(filePath)
                .fileType(FileType.ESSAY)
                .build());
    }

    /**
     * 이미지를 S3 스토리지에 업로드하고 파일경로를 반환한다.
     *
     * @param uploader  이s미지를 업로드한 유저
     * @param imageFile s3 스토리지에 저장할 이미지
     * @return 저장된 이미지의 s3 스토리지 경로
     */
    public File uploadImageFile(User uploader, MultipartFile imageFile) {
        // 이미지 파일이 비어있는지 확인
        if (imageFile.isEmpty()) {
            throw new CustomException("이미지 파일이 비어있습니다.");
        }

        List<String> imageExtensions = List.of("jpg", "jpeg", "png");
        String fileExt = getFileExtension(imageFile).toLowerCase();
        // 이미지 파일의 확장자가 jpg, jpeg, png인지 확인
        if (!imageExtensions.contains(fileExt)) {
            throw new CustomException("이미지 파일은 jpg, jpeg, png 파일만 업로드 가능합니다.");
        }
        // s3에 저장할 파일명 생성
        String s3FileName = "images/" + UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        // s3에 저장할 파일의 메타데이터 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageFile.getSize());
        metadata.setContentType(imageFile.getContentType());
        // s3 스토리지에 이미지 파일 업로드
        try {
            amazonS3.putObject(bucketName, s3FileName, imageFile.getInputStream(), metadata);
        } catch (Exception e) {
            // 첨삭파일 업로드 에러 발생
            throw new CustomException(bucketName + "에 이미지를 업로드하는데 실패했습니다.");
        }
        // 업로드한 이미지의 s3 스토리지 경로
        String filePath = amazonS3.getUrl(bucketName, s3FileName).toString();
        // File 엔티티 생성
        return fileRepository.save(File.builder()
                .user(uploader)
                .filePath(filePath)
                .fileType(FileType.IMAGE)
                .build());
    }

    /**
     * s3 스토리지에 저장된 파일을 삭제한다.
     *
     * @param fileUrl 삭제할 파일의 s3 스토리지 경로
     */
    private void deleteFileFromBucket(String fileUrl) {
        // 파일경로가 S3 HostName으로 시작하는지 점검
        if (!fileUrl.startsWith(hostName)) {
            throw new CustomException(fileUrl + "은 잘못된 경로입니다.");
        }
        // File 엔티티 조회 후 삭제
//        File file = fileRepository.findByFilePath(fileUrl)
//                .orElseThrow(() -> new CustomException("삭제할 파일이 존재하지 않습니다."));
//        fileRepository.deleteById(file.getId());
        // S3 버킷에 파일이 존재하는지 확인
        boolean fileExists = amazonS3.doesObjectExist(bucketName, fileUrl);
        // S3 버킷에서 파일 삭제
        if (fileExists) {
            amazonS3.deleteObject(bucketName, fileUrl);
        } else {
            throw new CustomException("S3 버킷에 삭제할 파일이 존재하지 않습니다.");
        }
    }

    /**
     * 파일 조회하기
     *
     * @param dirName  조회할 버킷의 디렉토리 이름 (images / essays)
     * @param fileName 조회할 파일 이름
     * @return 파일이 저장된 경로 반환
     */
    @Transactional(readOnly = true)
    public File findFileByFilePath(String dirName, String fileName) {
        String filePath = dirName + "/" + fileName;
        // File 엔티티 조회 후 반환
        return fileRepository.findByFilePath(filePath)
                .orElseThrow(() -> new CustomException("조회할 파일이 존재하지 않습니다."));
    }

    private String getFilePathFromBucket(String dirName, String fileName) {
        String filePath = dirName + "/" + fileName;
        boolean fileExists = amazonS3.doesObjectExist(bucketName, filePath);
        // 파일이 존재하는 경우 파일 경로 반환
        if (fileExists) {
            return amazonS3.getUrl(bucketName, filePath).toString();
        } else {
            throw new CustomException("조회할 파일이 존재하지 않습니다.");
        }
    }
}