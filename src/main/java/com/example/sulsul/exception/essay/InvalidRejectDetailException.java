package com.example.sulsul.exception.essay;

import com.example.sulsul.exception.BadInputException;

import java.util.Map;

public class InvalidRejectDetailException extends BadInputException {
    public InvalidRejectDetailException(Map<String, String> errorMap) {
        super("ESSAY_04", "거절 사유가 유효하지 않습니다.", errorMap);
    }
}