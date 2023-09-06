package com.example.sulsul.oauth.controller;

import com.example.sulsul.oauth.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
