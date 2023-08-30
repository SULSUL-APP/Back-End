package com.example.sulsul.config.oauth;

import com.example.sulsul.config.jwt.JwtTokenProvider;
import com.example.sulsul.config.security.CustomUserDetails;
import com.example.sulsul.exception.user.UserNotFoundException;
import com.example.sulsul.user.entity.Role;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("OAuth2 Login 성공!");
        CustomUserDetails oAuth2User = (CustomUserDetails) authentication.getPrincipal();
        loginSuccess(response, oAuth2User);
        log.info("response Header: {}", response.getHeader("BearerAccessHeader"));
    }

    private void loginSuccess(HttpServletResponse response, CustomUserDetails oAuth2User) throws IOException {
        String accessToken = tokenProvider.createAccessToken(oAuth2User.getUsername(), new Date());
        String refreshToken = tokenProvider.createRefreshToken(new Date());
        response.addHeader("BearerAccessHeader", "Bearer " + accessToken);
        response.addHeader("BearerRefreshHeader", "Bearer " + refreshToken);

        tokenProvider.sendAccessAndRefreshToken(response, accessToken, refreshToken);
    }

    private boolean isGuest(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException()
        );

        return user.getUserRole().equals(Role.GUEST);
    }
}
