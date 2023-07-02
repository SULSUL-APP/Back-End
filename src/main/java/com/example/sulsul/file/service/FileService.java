package com.example.sulsul.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.exception.custom.CustomException;
import com.example.sulsul.file.entity.File;
import com.example.sulsul.file.repository.FileRepository;
import com.example.sulsul.common.type.FileType;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${s3.host.name}")
    private String hostName;

    private final AmazonS3 amazonS3;
    private final AmazonS3Client amazonS3Client;
    private final FileRepository fileRepository;

    /**
     * 파일의 확장자 추출
     *
     * @param file 확장자를 추출할 파일
     * @return 파일의 확장자 반환
     */
    private String getFileExtension(MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            int pos = filename.lastIndexOf(".");
            return filename.substring(pos + 1);
        } catch (Exception e) {
            throw new CustomException("파일 확장자 추출에 실패했습니다.");
        }
    }

    /**
     * 첨삭파일을 S3 스토리지에 업로드하고 파일경로를 반환한다.
     *
     * @param essayFile S3 스토리지에 업로드할 첨삭파일
     * @return S3 스토리지에 업로드된 첨삭파일의 경로
     */
    public String uploadEssayFileToBucket(MultipartFile essayFile) {
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
        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    /**
     * 첨삭파일을 S3 스토리지에 업로드하고 파일 엔티티를 생성한다.
     *
     * @param uploader  첨삭파일을 업로드한 유저
     * @param essay     첨삭파일을 업로드한 에세이
     * @param essayFile s3 스토리지에 저장할 첨삭파일
     * @return 생성한 파일 엔티티 반환
     */
    @Transactional
    public File uploadEssayFile(User uploader, Essay essay, MultipartFile essayFile) {
        // 첨삭파일이 비어있는지 확인
        if (essayFile.isEmpty()) {
            throw new CustomException("첨삭파일이 비어있습니다.");
        }
        // 기존에 업로드한 첨삭파일이 있으면 삭제
        if (uploader.getUType().equals(UType.STUDENT)) {
            fileRepository.getStudentEssayFile(essay.getId(), uploader.getId())
                    .ifPresent(file -> deleteFile(file.getFilePath()));
        } else if (uploader.getUType().equals(UType.TEACHER)) {
            fileRepository.getTeacherEssayFile(essay.getId(), uploader.getId())
                    .ifPresent(file -> deleteFile(file.getFilePath()));
        }
        // 첨삭파일을 s3 스토리지에 업로드하고 파일경로를 반환
        String filePath = uploadEssayFileToBucket(essayFile);
        // File 엔티티 생성
        return fileRepository.save(File.builder()
                .essay(essay)
                .user(uploader)
                .filePath(filePath)
                .fileType(FileType.ESSAY)
                .build());
    }

    /**
     * 이미지 파일을 S3 버킷에 업로드하고 파일경로를 반환한다.
     *
     * @param imageFile S3 버킷에 업로드할 이미지 파일
     * @return S3 버킷에 업로드된 이미지 파일의 경로
     */
    public String uploadImageToBucket(MultipartFile imageFile) {
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
        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    /**
     * 이미지를 S3 스토리지에 업로드하고 파일경로를 반환한다.
     *
     * @param uploader  이미지를 업로드한 유저
     * @param imageFile s3 스토리지에 저장할 이미지
     * @return 저장된 이미지의 s3 스토리지 경로
     */
    @Transactional
    public File uploadImageFile(User uploader, MultipartFile imageFile) {
        // 이미지 파일이 비어있는지 확인
        if (imageFile.isEmpty()) {
            throw new CustomException("이미지 파일이 비어있습니다.");
        }
        // 기존에 업로드한 이미지 파일이 있으면 삭제
        if (uploader.getProfileImage() != null) {
            fileRepository.findByFilePath(uploader.getProfileImage())
                    .ifPresent(file -> deleteFile(file.getFilePath()));
        }
        // 이미지 파일을 s3 스토리지에 업로드하고 파일경로를 반환
        String filePath = uploadImageToBucket(imageFile);
        // File 엔티티 생성
        return fileRepository.save(File.builder()
                .user(uploader)
                .filePath(filePath)
                .fileType(FileType.IMAGE)
                .build());
    }

    private String decodeURL(String fileUrl) {
        // fileUrl에서 hostName 제거, 모든 space 제거, UTF8 형식으로 디코딩
        return URLDecoder.decode(fileUrl.replace(hostName, "")
                .replaceAll("\\p{Z}", ""), StandardCharsets.UTF_8);
    }

    /**
     * s3 버킷에 저장된 파일을 삭제한다.
     *
     * @param fileUrl 삭제할 파일의 s3 스토리지 경로
     */
    public void deleteFileFromBucket(String fileUrl) {
        String decodeURL = decodeURL(fileUrl);
        boolean fileExists = amazonS3.doesObjectExist(bucketName, decodeURL);
        // S3 버킷에서 파일 삭제
        if (fileExists) {
            amazonS3.deleteObject(bucketName, decodeURL);
        } else {
            throw new CustomException("S3 버킷에 삭제할 파일이 존재하지 않습니다.");
        }
    }

    /**
     * 파일 엔티티와 S3 버킷에서 파일을 삭제한다.
     *
     * @param filePath 삭제할 파일의 파일경로
     */
    @Transactional
    public void deleteFile(String filePath) {
        // 파일경로가 S3 HostName으로 시작하는지 점검
        if (!filePath.startsWith(hostName)) {
            throw new CustomException(filePath + "은 잘못된 경로입니다.");
        }
        // File 엔티티 조회 후 삭제
        File file = fileRepository.findByFilePath(filePath)
                .orElseThrow(() -> new CustomException("삭제할 파일이 존재하지 않습니다."));
        fileRepository.deleteById(file.getId());
        // S3 버킷에서 파일 삭제
        deleteFileFromBucket(filePath);
    }

    /**
     * s3 버킷에서 파일을 조회한다.
     *
     * @param filePath 조회할 파일의 경로
     * @return 파일이 저장된 경로 반환s
     */
    private String getFilePathFromBucket(String filePath) {
        boolean fileExists = amazonS3.doesObjectExist(bucketName, filePath);
        // 파일이 존재하는 경우 파일 경로 반환
        if (fileExists) {
            return amazonS3.getUrl(bucketName, filePath).toString();
        } else {
            throw new CustomException("조회할 파일이 존재하지 않습니다.");
        }
    }

    /**
     * 파일 엔티티를 조회한다.
     *
     * @param filePath 조회할 파일의 경로
     * @return 파일이 저장된 경로 반환
     */
    @Transactional(readOnly = true)
    public File findFileByFilePath(String filePath) {
        // File 엔티티 조회 후 반환
        return fileRepository.findByFilePath(filePath)
                .orElseThrow(() -> new CustomException("조회할 파일이 존재하지 않습니다."));
    }
}