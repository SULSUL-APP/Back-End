package com.example.sulsul.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
public class uploadFileRequest {
    private MultipartFile file;
    private String detail;
}