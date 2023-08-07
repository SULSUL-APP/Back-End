package com.example.sulsul.exception.user;

import com.example.sulsul.exception.ResourceNotFoundException;

import java.util.Map;

public class TeacherNotFoundException extends ResourceNotFoundException {
    public TeacherNotFoundException(long userId) {
        super("USER_02", "해당 강사를 찾을 수 없습니다.", Map.of("userId", String.valueOf(userId)));
    }
}