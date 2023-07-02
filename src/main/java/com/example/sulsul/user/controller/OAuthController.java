package com.example.sulsul.user.controller;

import com.example.sulsul.user.service.OAuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/login/oauth2", produces = "application/json")
public class OAuthController {

    OAuthService loginService;

    public OAuthController(OAuthService oAuthService) {
        this.loginService = oAuthService;
    }

    @GetMapping("/code/{registrationId}")
    public void googleLogin(@RequestParam String code, @PathVariable String registrationId) {
        loginService.socialLogin(code, registrationId);
    }
}
