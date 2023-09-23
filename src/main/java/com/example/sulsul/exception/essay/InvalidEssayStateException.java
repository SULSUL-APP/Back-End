package com.example.sulsul.exception.essay;

import com.example.sulsul.exception.ResourceNotFoundException;

import java.util.Map;

public class InvalidEssayStateException extends ResourceNotFoundException {
    public InvalidEssayStateException(long essayId) {
        super("ESSAY_05", "조회하려는 첨삭상태와 essayId에 해당하는 첨삭의 첨삭상태가 일치하지 않습니다.",
                Map.of("essayId", String.valueOf(essayId)));
    }
}