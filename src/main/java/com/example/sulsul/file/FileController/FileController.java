package com.example.sulsul.file.FileController;

import com.example.sulsul.file.dto.FileUploadRequest;
import com.example.sulsul.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload/essay")
    public String uploadEssayFile(@ModelAttribute FileUploadRequest request) {
        fileService.uploadEssayFileToBucket(request.getFile());
        return "essay upload success";
    }

    @PostMapping("/upload/image")
    public String uploadImageFile(@ModelAttribute FileUploadRequest request) {
        fileService.uploadImageToBucket(request.getFile());
        return "image upload success";
    }

    @DeleteMapping("/delete/file")
    public String deleteEssayFile(@RequestParam("path") String filePath) {
        fileService.deleteFileFromBucket(filePath);
        return "essay delete success";
    }
}