package com.example.sulsul.exception.user;

import com.example.sulsul.exception.ResourceNotFoundException;

public class UTypeNotFoundException extends ResourceNotFoundException {
    public UTypeNotFoundException() {
        super("USER_04", "해당 유저 타입을 찾을 수 없습니다.");
    }
}
