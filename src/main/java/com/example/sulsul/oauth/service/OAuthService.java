package com.example.sulsul.oauth.service;

import com.example.sulsul.common.type.EType;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.config.jwt.JwtTokenProvider;
import com.example.sulsul.user.dto.request.OauthDto;
import com.example.sulsul.user.dto.request.SignUpDto;
import com.example.sulsul.user.dto.response.LoginResponseDto;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class OAuthService {
//
//    private final JwtTokenProvider jwtTokenProvider;
//    private final Oauth2UserFactory oauth2UserFactory;
//
//    public LoginResponseDto socialLogin(OauthDto oauthDto) {
//
//        User user = oauth2UserFactory.getOauth2User(oauthDto);
//        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), new Date());
//        String refreshToken = jwtTokenProvider.createRefreshToken(new Date());
//        boolean initData = initDataInput(user);
//        //note : 소셜로그인 정보에 추가정보 기입 유무 포함.
//
//        return new LoginResponseDto(user.getName(), user.getEmail(), accessToken, refreshToken, initData);
//    }
//
//    /**
//     * 초기 데이터는 Name 과 Email 뿐이다. 나머지 추가정보를 받아서 업데이트한다.
//     */
//    public void signUpData(User user, SignUpDto signUpDto) {
//
//        user.updateUType(UType.getUType(signUpDto.getUType()));
//        user.updateEType(EType.getEType(signUpDto.getEType()));
//
//    }
//
//    /**
//     * 회원가입 직후 필수 추가 정보를 입력하였는지 확인한다. 모두 입력하였다면 true 를 반환한다.
//     */
//    public boolean initDataInput(User user) {
//        return (user.getUserType() != null && user.getEssayType() != null);
//    }
}
