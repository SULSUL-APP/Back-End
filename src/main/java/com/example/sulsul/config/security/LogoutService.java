package com.example.sulsul.config.security;

import com.example.sulsul.config.jwt.JwtTokenProvider;
import com.example.sulsul.exception.refresh.RefreshTokenNotFoundException;
import com.example.sulsul.refreshtoken.RefreshToken;
import com.example.sulsul.refreshtoken.RefreshTokenRepository;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {

        String accessToken = jwtTokenProvider.resolveToken(request);
        User user = jwtTokenProvider.getUserFromAccessToken(accessToken);
        RefreshToken storedToken = refreshTokenRepository.findByUserId(user.getId())
                .orElseThrow(RefreshTokenNotFoundException::new);

        System.out.println("logout: " + storedToken.getRefreshToken());

        // refresh token 삭제
        if (storedToken != null) {
            refreshTokenRepository.delete(storedToken);
        }
    }
}