package com.example.sulsul.refreshtoken;

import com.example.sulsul.config.jwt.JwtTokenProvider;
import com.example.sulsul.config.jwt.dto.JwtTokenDto;
import com.example.sulsul.config.security.CustomUserDetails;
import com.example.sulsul.exception.refresh.InvalidRefreshTokenException;
import com.example.sulsul.exception.refresh.RefreshTokenExpiredException;
import com.example.sulsul.exception.refresh.RefreshTokenNotFoundException;
import com.example.sulsul.exception.refresh.RefreshTokenVerificationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    /**
     * user의 refreshToken이 맞다면 accessToken을 재발급한다.
     * refreshToken의 만료일이 3일 이내라면 refreshToken 을 재발급한다.
     *
     * @param user         로그인한 유저
     * @param refreshToken 유저의 refreshToken
     * @return TokenDto 반환
     */
    @Transactional
    public JwtTokenDto refresh(CustomUserDetails user, String refreshToken) {

        JwtTokenDto tokensDto = new JwtTokenDto();
        Claims claims;
        try {
            System.out.println(refreshToken);
            claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(refreshToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RefreshTokenExpiredException();
        } catch (Exception e) {
            throw new InvalidRefreshTokenException();
        }

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUser(user.getUser())
                .orElseThrow(RefreshTokenNotFoundException::new);

        if (!refreshTokenEntity.getRefreshToken().equals(refreshToken)) {
            throw new RefreshTokenVerificationException();
        }

        String email = user.getUser().getEmail();
        String accessToken = jwtTokenProvider.createAccessToken(email, new Date());
        tokensDto.setAccessToken(accessToken);

        long expiration = claims.getExpiration().getTime();
        long now = new Date().getTime();
        long diffTime = expiration - now;

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