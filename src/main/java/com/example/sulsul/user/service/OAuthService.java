package com.example.sulsul.user.service;

import com.example.sulsul.common.type.EType;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.config.auth.JwtProvider;
import com.example.sulsul.user.dto.request.OauthDto;
import com.example.sulsul.user.dto.request.SignUpDto;
import com.example.sulsul.user.dto.response.LoginResponseDto;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.oauth2.Oauth2UserFactory;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@RequiredArgsConstructor
public class OAuthService {

    private final JwtProvider jwtProvider;
    private final Oauth2UserFactory oauth2UserFactory;

    public LoginResponseDto socialLogin(OauthDto oauthDto) {

        User user = oauth2UserFactory.getOauth2User(oauthDto);

        String accessToken = jwtProvider.createAccessToken(user);
        String refreshToken = jwtProvider.createRefreshToken(user);
        boolean initData = initDataInput(user);
        //note : 소셜로그인 정보에 추가정보 기입 유무 포함.

        return new LoginResponseDto(user.getName(), user.getEmail(), accessToken, refreshToken, initData);
    }

    /**
     * 초기 데이터는 Name 과 Email 뿐이다. 나머지 추가정보를 받아서 업데이트한다.
     */
    public void signUpData(User user, SignUpDto signUpDto) {

        user.updateUType(UType.getUType(signUpDto.getUType()));
        user.updateEType(EType.getEType(signUpDto.getEType()));

    }

    /**
     * 회원가입 직후 필수 추가 정보를 입력하였는지 확인한다. 모두 입력하였다면 true 를 반환한다.
     */
    public boolean initDataInput(User user) {
        return (user.getUserType() != null && user.getEssayType() != null);
    }
}
