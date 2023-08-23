package com.example.sulsul.config.security;

import com.example.sulsul.user.entity.User;
import lombok.Getter;

@Getter
public class UserAccount extends CustomUserDetails {
    private final User user;

    public UserAccount(User user) {
        super(user);
        this.user = user;
    }
}
