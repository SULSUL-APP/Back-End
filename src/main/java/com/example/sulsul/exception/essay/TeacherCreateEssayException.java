package com.example.sulsul.exception.essay;

import com.example.sulsul.exception.AccessNotAllowedException;

import java.util.Map;

public class TeacherCreateEssayException extends AccessNotAllowedException {
    public TeacherCreateEssayException(long userId) {
        super("ESSAY_03", "강사는 첨삭요청을 보낼 수 없습니다.", Map.of("userId", String.valueOf(userId)));
    }
}
