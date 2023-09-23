package com.example.sulsul.exception.profile;

import com.example.sulsul.exception.ResourceNotFoundException;

import java.util.Map;

public class TeacherProfileNotFoundException extends ResourceNotFoundException {
    public TeacherProfileNotFoundException(long userId) {
        super("PROFILE_02", "유저(강사)의 프로필을 찾을 수 없습니다.", Map.of("userId", String.valueOf(userId)));
    }

    public TeacherProfileNotFoundException() {
        super("PROFILE_02", "유저(강사)의 프로필을 찾을 수 없습니다.");
    }
}