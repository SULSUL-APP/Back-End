package com.example.sulsul.exception.file;

import com.example.sulsul.exception.BaseErrorCode;
import com.example.sulsul.exception.BaseException;
import lombok.Getter;

import java.util.Map;

@Getter
public class FileException extends BaseException {

    protected Map<String, String> errors = Map.of();

    public FileException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
