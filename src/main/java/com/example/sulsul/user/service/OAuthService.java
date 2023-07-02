package com.example.sulsul.user.service;

import org.springframework.stereotype.Service;

@Service
public class OAuthService {

    public void socialLogin(String code, String registrationId) {
        System.out.println("code = " + code);
        System.out.println("registrationId = " + registrationId);
    }
}
