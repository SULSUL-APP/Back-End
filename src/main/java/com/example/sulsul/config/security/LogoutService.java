package com.example.sulsul.config.security;

import com.example.sulsul.config.jwt.JwtTokenProvider;
import com.example.sulsul.exception.refresh.RefreshTokenNotFoundException;
import com.example.sulsul.refreshtoken.RefreshToken;
import com.example.sulsul.refreshtoken.RefreshTokenRepository;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
@Slf4j
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

        log.info("[logoutService] RefreshToken 조회: {}", storedToken.getRefreshToken());

        // refresh token 삭제
        if (storedToken != null) {
            log.info("[logoutService] RefreshToken 삭제");
            refreshTokenRepository.delete(storedToken);
            log.info("[logoutService] 로그아웃 완료");
        }
    }
}