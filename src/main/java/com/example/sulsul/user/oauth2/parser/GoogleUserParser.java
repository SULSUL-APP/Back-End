package com.example.sulsul.user.oauth2.parser;

import com.example.sulsul.common.type.LoginType;
import com.example.sulsul.exception.user.UserNotFoundException;
import com.example.sulsul.user.dto.request.OauthDto;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.oauth2.userinfo.GoogleUserInfo;
import com.example.sulsul.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoogleUserParser {
    private final UserRepository userRepository;

    @Transactional
    public User getGoogleUser(OauthDto oauthDto) {

        Map<String, Object> attributes = getGoogleAttributes(oauthDto);
        GoogleUserInfo googleUserInfo = new GoogleUserInfo(attributes);

        String email = googleUserInfo.getEmail();
        String name = googleUserInfo.getName();

        if (!userRepository.existsByEmail(email)) {
            log.info("입력받은 Email 로 가입된 Google 회원이 없습니다.");

            User user = User.builder()
                    .name(name)
                    .email(email)
                    .loginType(LoginType.GOOGLE)
                    .build();

            User savedUser = userRepository.save(user);
            log.info("Google 회원등록, user={}", savedUser);

            return savedUser;
        }

        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return user;
    }


    public Map<String, Object> getGoogleAttributes (OauthDto oauthDto) {
        return WebClient.create()
                .get()
                .uri("https://oauth2.googleapis.com/tokeninfo?id_token=" + oauthDto.getIdToken())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(oauthDto.getAccessToken()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }
}
