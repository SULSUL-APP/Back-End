package com.example.sulsul.user.dto;

import com.example.sulsul.user.entity.User;
import lombok.Getter;

@Getter
public class StudentResponse extends UserResponse {
    public StudentResponse(User user) {
        super(user);
    }
}