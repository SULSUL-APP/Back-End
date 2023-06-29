package com.example.sulsul.user.dto;

import com.example.sulsul.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final String profileImage;
    private final String uType;
    private final String eType;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.profileImage = user.getProfileImage();
        this.uType = user.getUType().name();
        this.eType = user.getEType().name();
    }
}