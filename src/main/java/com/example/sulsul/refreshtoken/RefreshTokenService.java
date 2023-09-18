package com.example.sulsul.refreshtoken;

import com.example.sulsul.config.jwt.JwtTokenProvider;
import com.example.sulsul.config.jwt.dto.JwtTokenDto;
import com.example.sulsul.exception.refresh.RefreshTokenMismatchException;
import com.example.sulsul.exception.refresh.RefreshTokenNotFoundException;
import com.example.sulsul.exception.user.UserNotFoundException;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * user의 refreshToken이 맞다면 accessToken을 재발급한다.
     * refreshToken의 만료일이 3일 이내라면 refreshToken을 재발급한다.
     *
     * @param refreshToken 유저의 refreshToken
     * @return TokenDto 반환
     */
    @Transactional
    public JwtTokenDto refresh(String refreshToken, Claims claims) {
        JwtTokenDto tokensDto = new JwtTokenDto();
        String email = claims.getSubject();

        // subject로 유저정보 조회, 해당유저가 없다면 예외처리
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        // 유저의 refreshToken 조회
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUser(user)
                .orElseThrow(RefreshTokenNotFoundException::new);

        // 유저의 refreshToken이 맞는지 확인
        if (!refreshTokenEntity.getRefreshToken().equals(refreshToken)) {
            throw new RefreshTokenMismatchException();
        }

        // accessToken 재발급
        String accessToken = jwtTokenProvider.createAccessToken(email, new Date());
        tokensDto.setAccessToken(accessToken);

        // refresh 토큰 만료일 계산
        long expiration = jwtTokenProvider.getTokenClaims(refreshToken)
                .getExpiration().getTime();
        long now = new Date().getTime();
        long diffTime = expiration - now;

        // 1일 이내에 만료된다면 refreshToken 재발급
        if (diffTime < 86400000) {
            String newRefreshToken = jwtTokenProvider.createRefreshToken(new Date());
            refreshTokenEntity.updateRefreshToken(newRefreshToken);
            // 재발급된 refresh token 저장
            refreshTokenRepository.save(refreshTokenEntity);
            tokensDto.setRefreshToken(newRefreshToken);
        }

        return tokensDto;
    }
}