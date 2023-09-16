package com.example.sulsul.config.oauth;

import com.example.sulsul.user.entity.Role;
import com.example.sulsul.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Builder
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

//    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey,
//                           String name, String email, String picture) {
//        this.attributes = attributes;
//        this.nameAttributeKey = nameAttributeKey;
//        this.name = name;
//        this.email = email;
//        this.picture = picture;
//    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                     Map<String, Object> attributes) {

        if("kakao".equals(registrationId)) {
            return ofKakao("id", attributes);
        }

        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {

        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {

        // kakao는 kakao_account에 유저정보가 있다. (email)
        Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");

        // kakao_account안에 또 profile이라는 JSON객체가 있다. (nickname, profile_image)
        Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        log.info("[카카오 kakaoProfile] : {}", kakaoProfile);
        log.info("[카카오 attributes] kakaoAccount: {}", kakaoAccount);
        log.info("[카카오 유저 정보] email: {}", email);
        log.info("[카카오 유저 정보] userNameAttributeName: {}", userNameAttributeName);

        return OAuthAttributes.builder()
                .name((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String) kakaoProfile.get("profile_image_url"))
                .attributes(kakaoAccount)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .profileImage(picture)
                .userRole(Role.GUEST)
                .build();
    }

    @Override
    public String toString() {
        return "OAuthAttributes{" +
                "attributes=" + attributes +
                ", nameAttributeKey='" + nameAttributeKey + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }
}
