package com.example.sulsul.exception.file;

import com.example.sulsul.exception.BadInputException;

import java.util.Map;

public class InvalidFilePathException extends BadInputException {
    public InvalidFilePathException(String filePath) {
        super("FILE_07", "잘못된 파일경로입니다.", Map.of("filePath", filePath));
    }
}