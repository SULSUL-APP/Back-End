package com.example.sulsul.config.jwt;

import com.example.sulsul.config.jwt.dto.JwtTokenDto;
import com.example.sulsul.config.security.CustomUserDetails;
import com.example.sulsul.config.security.CustomUserDetailsServiceImpl;
import com.example.sulsul.exception.jwt.ExpiredTokenException;
import com.example.sulsul.exception.jwt.TokenNotValidException;
import com.example.sulsul.exception.refresh.InvalidRefreshTokenException;
import com.example.sulsul.exception.refresh.RefreshTokenExpiredException;
import com.example.sulsul.exception.refresh.RefreshTokenNotFoundException;
import com.example.sulsul.user.entity.User;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final CustomUserDetailsServiceImpl userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;
    private final long accessValidTime = 1000L * 60 * 60;    // 액세스 토큰 유효 시간 60분
    //    private final long accessValidTime = 1000L * 10;    // 테스트를 위해 액세스 토큰 유효 시간을 10초로 설정
    private final long refreshValidTime = 1000L * 60 * 60 * 24 * 14;    // 리프레쉬 토큰 유효 시간 2주
    //    private final long refreshValidTime = 1000L * 10;   // 테스트를 위해 리프레쉬 토큰 유효 시간을 10초로 설정

    /**
     * secretKey를 Base64로 인코딩
     */
    @PostConstruct
    protected void init() {
        log.info("[init] JwtTokenProvider 내 secretKey 초기화 시작");
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        log.info("[init] JwtTokenProvider 내 secretKey 초기화 완료");
    }

    /**
     * AccessToken과 RefreshToken을 생성 후
     * JwtTokenDto에 담아 반환
     */
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

    /**
     * AccessToken 생성
     */
    public String createAccessToken(String email, Date now) {
        log.info("[createAccessToken] 액세스 토큰 생성 시작");

        Claims claims = Jwts.claims().setSubject(email);
        log.info("Access Token에서 claims 생성 claims : {}", claims);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuer("sulsul")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        log.info("[createToken] 액세스 토큰 생성 완료: {}", accessToken);

        return accessToken;
    }

    /**
     * RefreshToken 생성
     */
    public String createRefreshToken(Date now) {
        log.info("[createRefreshToken] 리프레쉬 토큰 생성 시작");

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer("sulsul")
                .setExpiration(new Date(now.getTime() + refreshValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        log.info("[createRefreshToken] 리프레쉬 토큰 생성 완료: {}", refreshToken);

        return refreshToken;
    }

    /**
     * AccessToken으로 인증 정보 조회
     */
    public Authentication getAuthentication(String token) {
        log.info("[getAuthentication] 토큰 인증 정보 조회 시작");
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserEmail(token));

        log.info("[getAuthentication] 토큰 인증 정보 조회 완료, UserDetails User Email : {}", userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    /**
     * AccessToken에서 유저정보 추출 (LogoutService)
     * AccessToken이 만료된 경우에도 로그아웃이 가능하도록 처리
     */
    public User getUserFromAccessToken(String accessToken) {
        try {
            String email = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .getSubject();
            return userDetailsService.loadUserByUsername(email)
                    .getUser();
        } catch (ExpiredJwtException e) {
            // AccessToken이 만료된 경우에도 로그아웃이 가능하도록 처리
            String email = e.getClaims().getSubject();
            return userDetailsService.loadUserByUsername(email)
                    .getUser();
        } catch (MalformedJwtException | SignatureException e) {
            throw new TokenNotValidException();
        }
    }

    /**
     * AccessToken에서 회원 이메일 추출
     */
    private String getUserEmail(String token) {
        log.info("[getUserEmail] 토큰 기반 회원 구별 정보 추출");

        String email = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        log.info("[getUserEmail] 토큰 기반 회원 구별 정보 추출 완료, Email : {}", email);

        return email;
    }

    /**
     * AccessToken 추출
     */
    public String resolveAccessToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.replace("Bearer ", "");
            log.info("[resolveToken] HTTP 헤더에서 Token 값 추출 완료: {}", token);
            return token;
        }

        return null;
    }

    /**
     * RefreshToken 추출
     */
    public String resolveRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("RefreshToken");

        if (refreshToken == null) {
            throw new RefreshTokenNotFoundException();
        }

        return refreshToken;
    }

    /**
     * AccessToken의 유효성 + 만료여부 체크
     */
    public boolean validateToken(String token) {
        log.info("[validateToken] 토큰 유효 체크 시작");

        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (MalformedJwtException | SignatureException e) {
            throw new TokenNotValidException();
        }

        return true;
    }

    /**
     * AccessToken + RefreshToken 헤더에 실어서 보내기
     */
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
        log.info("Access Token, Refresh Token 헤더 설정 완료");
    }

    /**
     * AccessToken 헤더 설정
     */
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader("AccessToken", "Bearer " + accessToken);
    }

    /**
     * RefreshToken 헤더 설정
     */
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader("RefreshToken", refreshToken);
    }

    /**
     * RefreshToken 유효성 + 만료여부 체크
     */
    public boolean validateRefreshToken(String refreshToken) {
        return this.getRefreshTokenClaims(refreshToken) != null;
    }

    /**
     * RefreshToken의 claims 반환
     * RefreshToken 유효성 + 만료여부 체크
     */
    public Claims getRefreshTokenClaims(String refreshToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(refreshToken)
                    .getBody();
        } catch (MalformedJwtException e) {
            throw new InvalidRefreshTokenException();
        } catch (ExpiredJwtException e) {
            throw new RefreshTokenExpiredException();
        }
    }

    /**
     * 만료된 토큰의 claims 반환
     */
    public Claims getExpiredTokenClaims(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // 만료된 경우에도 claims 반환가능
        } catch (MalformedJwtException e) {
            throw new TokenNotValidException();
        }
        return null;
    }
}