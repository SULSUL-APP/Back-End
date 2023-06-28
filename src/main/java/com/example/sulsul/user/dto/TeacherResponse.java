package com.example.sulsul.user.dto;

import com.example.sulsul.user.entity.User;
import lombok.Getter;

@Getter
public class TeacherResponse extends UserResponse {

    private String catchPhrase;

    public TeacherResponse(User user) {
        super(user);
        this.catchPhrase = user.getCatchPhrase();
    }
}