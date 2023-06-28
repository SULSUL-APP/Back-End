package com.example.sulsul.file.FileController;

import com.example.sulsul.file.dto.UploadFileRequest;
import com.example.sulsul.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload/essay")
    public String uploadEssayFile(@ModelAttribute UploadFileRequest request) {
        fileService.uploadEssayFileToBucket(request.getFile());
        return "essay upload success";
    }

    @PostMapping("/upload/image")
    public String uploadImageFile(@ModelAttribute UploadFileRequest request) {
        fileService.uploadImageToBucket(request.getFile());
        return "image upload success";
    }
}