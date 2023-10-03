package com.example.sulsul.config.security;

/*
토큰으로 사용될 유저 정보와 권한을 저장
 */

import com.example.sulsul.config.oauth.OAuthAttributes;
import com.example.sulsul.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final User user;
    private OAuthAttributes attributes;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public CustomUserDetails(User user, OAuthAttributes attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // 계정이 가지고 있는 권한 목록 리턴
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {  // 계정이 만료되었는지 리턴 true는 만료되지 않음을 나타냄
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {   // 계정이 잠겼는지 리턴 true는 잠기지 않음을 나타냄
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {  // 비밀번호가 만료되었는지 리턴 true는 만료되지 않음을 나타냄
        return true;
    }

    @Override
    public boolean isEnabled() {    // 계정 활성화 여부를 리턴 true는 활성화됨을 나타냄
        return true;
    }

    @Override
    public String getName() {
        return this.user.getName();
    }

}