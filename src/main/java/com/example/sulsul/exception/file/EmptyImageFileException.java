package com.example.sulsul.exception.file;

import com.example.sulsul.exception.BadInputException;

public class EmptyImageFileException extends BadInputException {
    public EmptyImageFileException() {
        super("FILE_02", "이미지 파일이 비어있습니다.");
    }
}