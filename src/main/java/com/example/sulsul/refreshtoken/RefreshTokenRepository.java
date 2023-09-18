package com.example.sulsul.refreshtoken;

import com.example.sulsul.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    /**
     * user의 RefreshToken 조회
     *
     * @param user 조회할 유저
     * @return RefreshToken을 Optional 형태로 반환
     */
    Optional<RefreshToken> findByUser(User user);

    /**
     * refreshToken값으로 RefreshToken 엔티티 조회
     *
     * @param refreshToken refreshToken 값
     * @return RefreshToken을 Optional 형태로 반환
     */
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

}