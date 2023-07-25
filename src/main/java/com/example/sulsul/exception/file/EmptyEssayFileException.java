package com.example.sulsul.exception.file;

import com.example.sulsul.exception.BadInputException;

public class EmptyEssayFileException extends BadInputException {
    public EmptyEssayFileException() {
        super("FILE_01", "첨삭파일이 첨부되지 않았습니다.");
    }
}