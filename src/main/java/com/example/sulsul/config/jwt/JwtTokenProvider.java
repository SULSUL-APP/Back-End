package com.example.sulsul.config.jwt;


import com.example.sulsul.config.jwt.dto.JwtTokenDto;
import com.example.sulsul.config.security.CustomUserDetails;
import com.example.sulsul.config.security.CustomUserDetailsServiceImpl;
import com.example.sulsul.exception.BaseException;
import com.example.sulsul.exception.jwt.TokenNotValidException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

/*
    JWT 토큰 생성
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final CustomUserDetailsServiceImpl userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    private final long tokenValidTime = 1000L * 60 * 60;    // 액세스 토큰 유효 시간 60분

    private final long refreshValidTime = 1000L * 60 * 60 * 24 * 14;    // 리프레쉬 토큰 유효 시간 2주

    // 객체 초기화 -> secretKey를 Base64로 인코딩
    @PostConstruct
    protected void init() {
        log.info("[init] JwtTokenProvider 내 secretKey 초기화 시작");
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        log.info("[init] JwtTokenProvider 내 secretKey 초기화 완료");
    }

    // 토큰 생성
    public JwtTokenDto createJwtToken(String email) {
        log.info("[createJwtToken] Jwt 토큰 생성 시작");

        final Date now = new Date();
        log.info("Date 생성 Date : {} ", now);

        JwtTokenDto jwtTokenDto = JwtTokenDto.builder()
                .accessToken(createAccessToken(email, now))
                .refreshToken(createRefreshToken(now))
                .build();

        log.info("[createJwtToken] Jwt 토큰 생성 완료: {}, {}", jwtTokenDto.getAccessToken(), jwtTokenDto.getRefreshToken());

        return jwtTokenDto;
    }

    public String createAccessToken(String email, Date now) {
        log.info("[createAccessToken] 액세스 토큰 생성 시작");

        Claims claims = Jwts.claims().setSubject(email);
        log.info("Access Token에서 claims 생성 claims : {}", claims);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuer("Dansup")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        log.info("[createToken] 액세스 토큰 생성 완료: {}", accessToken);

        return accessToken;
    }

    public String createRefreshToken(Date now) {
        log.info("[createRefreshToken] 리프레쉬 토큰 생성 시작");

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("Dansup")
                .setExpiration(new Date(now.getTime() + refreshValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        log.info("[createRefreshToken] 리프레쉬 토큰 생성 완료: {}", refreshToken);

        return refreshToken;
    }

    // JWT 토큰으로 인증 정보 조회
    public Authentication getAuthentication(String token) {
        log.info("[getAuthentication] 토큰 인증 정보 조회 시작");
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserEmail(token));

        log.info("[getAuthentication] 토큰 인증 정보 조회 완료, UserDetails User Email : {}", userDetails.getUsername());

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // JWT 토큰에서 회원 이메일 추출
    private String getUserEmail(String token) {
        log.info("[getUserEmail] 토큰 기반 회원 구별 정보 추출");

        String email = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        log.info("[getUserEmail] 토큰 기반 회원 구별 정보 추출 완료, Email : {}", email);

        return email;
    }

    public String resolveToken(HttpServletRequest request) {
        log.info("[resolveToken] HTTP 헤더에서 Token 값 추출");

        String authorization = request.getHeader("Authorization");

        if(authorization != null) {
            String token = authorization.split(" ")[1].trim();
            log.info("[resolveToken] HTTP 헤더에서 Token 값 추출 완료: {}", token);
            return token;
        }

        return null;
    }

    // JWT 토큰의 유효성 + 만료일 체크
    public boolean validateToken(String token) {
        log.info("[validateToken] 토큰 유효 체크 시작");

        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (BaseException e) {
            throw new TokenNotValidException();
        }
    }

}
