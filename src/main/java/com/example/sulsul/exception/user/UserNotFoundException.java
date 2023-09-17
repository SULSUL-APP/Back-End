package com.example.sulsul.exception.user;

import com.example.sulsul.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException() {
        super("USER_03", "해당 유저를 찾을 수 없습니다.");
    }
}