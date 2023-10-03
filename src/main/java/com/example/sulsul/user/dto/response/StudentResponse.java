package com.example.sulsul.user.dto.response;

import com.example.sulsul.user.entity.User;
import lombok.Getter;

@Getter
public class StudentResponse extends LoginResponse {
    public StudentResponse(User user) {
        super(user);
    }
}