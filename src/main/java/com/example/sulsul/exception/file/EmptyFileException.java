package com.example.sulsul.exception.file;

public class EmptyFileException extends FileException {
    public EmptyFileException() {
        super(FileErrorCode.EMPTY_FILE);
    }
}