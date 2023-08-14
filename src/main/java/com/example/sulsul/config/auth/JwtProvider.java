package com.example.sulsul.config.auth;

import com.example.sulsul.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {

    private final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    private final Long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 60 * 12;
    private final Long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 14;

    @PostConstruct
    protected void init() {
        log.info("[init] JwtTokenProvider 내 secretKey 초기화 시작");
        byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes(StandardCharsets.UTF_8));
        this.key = Keys.hmacShaKeyFor(keyBytes);    // keyBytes 와 이에 맞는 암호화 함수를 객체 형태로 반환
        log.info("[init] JwtTokenProvider 내 secretKey 초기화 완료");
    }

    public String createAccessToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION);
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiration)
                .setSubject(user.getEmail())
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiration)
                .setSubject(user.getEmail())
                .signWith(key)
                .compact();
    }

}
