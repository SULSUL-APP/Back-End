package com.example.sulsul.exception.file;

import com.example.sulsul.exception.SBaseErrorCode;
import com.example.sulsul.exception.SBaseException;
import lombok.Getter;

import java.util.Map;

@Getter
public class FileException extends SBaseException {

    protected Map<String, String> errors = Map.of();

    public FileException(SBaseErrorCode errorCode) {
        super(errorCode);
    }
}