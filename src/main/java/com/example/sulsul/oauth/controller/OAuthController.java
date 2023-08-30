package com.example.sulsul.oauth.controller;

import com.example.sulsul.user.dto.request.OauthDto;
import com.example.sulsul.user.dto.request.SignUpDto;
import com.example.sulsul.user.dto.response.LoginResponseDto;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.oauth.service.OAuthService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users", produces = "application/json")
public class OAuthController {

    private final OAuthService oAuthService;


//    @PostMapping(value = "/login/oauth")
//    public ResponseEntity<LoginResponseDto> oauth2Login(@RequestBody OauthDto oauthDto) {
//        LoginResponseDto loginResponseDto = oAuthService.socialLogin(oauthDto);
//        return ResponseEntity.ok().body(loginResponseDto);
//        //note : LoginResponse 에 추가정보 기입 유무 정보 포함.
//    }
//
//    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<Void> signUpUser(
//            @ModelAttribute @Valid SignUpDto signUpDto,
//            @Parameter(hidden = true) @AuthenticationPrincipal User user
//    ) {
//        oAuthService.signUpData(user, signUpDto);
//        return ResponseEntity.noContent().build();
//    }

}
