package com.example.sulsul.exception.file;

import com.example.sulsul.exception.BadInputException;

import java.util.Map;

public class ImageFileExtensionException extends BadInputException {
    public ImageFileExtensionException(String fileExt) {
        super("FILE_06", "이미지 파일은 jpg, jpeg, png 파일만 업로드 가능합니다.", Map.of("fileExt", fileExt));
    }
}