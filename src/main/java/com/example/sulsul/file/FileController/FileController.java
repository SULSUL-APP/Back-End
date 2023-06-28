package com.example.sulsul.file.FileController;

import com.example.sulsul.common.type.EType;
import com.example.sulsul.common.type.LoginType;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.entity.type.EssayState;
import com.example.sulsul.essay.entity.type.ReviewState;
import com.example.sulsul.file.dto.uploadFileRequest;
import com.example.sulsul.file.service.FileService;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload/essay")
    public String uploadEssayFile(@ModelAttribute uploadFileRequest request) {
        fileService.uploadEssayFileToBucket(request.getFile());
        return "essay upload success";
    }

    @PostMapping("/upload/image")
    public String uploadImageFile(@ModelAttribute uploadFileRequest request) {
        fileService.uploadImageToBucket(request.getFile());
        return "image upload success";
    }
}