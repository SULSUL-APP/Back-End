package com.example.sulsul.user.oauth2;

import com.example.sulsul.exception.notFound.UserNotFoundException;
import com.example.sulsul.user.dto.request.OauthDto;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.oauth2.parser.GoogleUserParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Oauth2UserFactory {

//    private final KakaoUserParser kakaoUserParser;
    private final GoogleUserParser googleUserParser;
//    private final AppleUserParser appleUserParser;

    public User getOauth2User(OauthDto oauthDto) {

        switch (oauthDto.getOrigin()) {
//            case "kakao":
//                return kakaoUserParser.getKakaoUser(oauthDto);
            case "google":
                return googleUserParser.getGoogleUser(oauthDto);
//            case "apple":
//                return appleUserParser.getAppleUser(oauthDto);
            default:
                throw new UserNotFoundException();
        }

    }

}
