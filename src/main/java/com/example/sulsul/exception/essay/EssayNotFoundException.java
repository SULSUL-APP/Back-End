package com.example.sulsul.exception.essay;

import com.example.sulsul.exception.ResourceNotFoundException;

import java.util.Map;

public class EssayNotFoundException extends ResourceNotFoundException {
    public EssayNotFoundException(long essayId) {
        super("ESSAY_02", "해당 첨삭을 찾을 수 없습니다.", Map.of("essayId", String.valueOf(essayId)));
    }
}