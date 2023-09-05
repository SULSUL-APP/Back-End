package com.example.sulsul.config.oauth;

import com.example.sulsul.config.security.CustomUserDetails;
import com.example.sulsul.exception.user.UserNotFoundException;
import com.example.sulsul.user.entity.Role;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.
                getClientRegistration().getRegistrationId();

        String userNameAttributeName = userRequest.
                getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        log.info("[OAuthAttributes] Account: {}", attributes);

        User user = saveOrUpdate(attributes);

        return new CustomUserDetails(user, attributes);
    }

    private User saveOrUpdate(OAuthAttributes attributes) {

        log.info("[카카오 attributes] kakakAccont: {}", attributes);

//        User user = userRepository.findByEmail(attributes.getEmail())
//                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
//                .orElse(attributes.toEntity());

        User user;
        if (userRepository.existsByEmail(attributes.getEmail())) {
            user = userRepository.findByEmail(attributes.getEmail()).orElseThrow(
                    UserNotFoundException::new
            );
            log.info("[카카오 유저 등록 확인] user_id: {}", user.getId());

        } else {
//            user = attributes.toEntity();
            user = User.builder()
                    .name(attributes.getName())
                    .email(attributes.getEmail())
                    .profileImage(attributes.getPicture())
                    .userRole(Role.GUEST)
                    .build();
            log.info("[카카오 유저 등록] user_id: {}", user.getId());
        }

        return userRepository.save(user);
    }

//        Map<String, Object> attributes = oAuth2User.getAttributes();
//
//        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
//        String email = (String) kakaoAccount.get("email");
//
//        log.info("[카카오 유저 정보] email: {}", email);
//
//        User user;
//        if(userRepository.existsByEmail(email)) {
//            user = userRepository.findByEmail(email).orElseThrow(
//                    () -> new UserNotFoundException()
//            );
//            log.info("[카카오 유저 등록 확인] user_id: {}", user.getId());
//
//        } else {
//            user = userService.createOAuthUser(email);
//            log.info("[카카오 유저 등록] user_id: {}", user.getId());
//        }
//
//        return new CustomUserDetails(user, attributes);
//    }

}