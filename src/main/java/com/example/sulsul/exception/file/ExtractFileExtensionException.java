package com.example.sulsul.exception.file;

import com.example.sulsul.exception.BadInputException;

import java.util.Map;

public class ExtractFileExtensionException extends BadInputException {
    public ExtractFileExtensionException(String fileName) {
        super("FILE_04", "파일 확장자 추출에 실패했습니다.", Map.of("fileName", fileName));
    }
}