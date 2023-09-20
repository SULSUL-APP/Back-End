package com.example.sulsul.refreshtoken;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    /**
     * user의 RefreshToken 조회
     *
     * @param userId 조회할 유저의 id
     * @return RefreshToken을 Optional 형태로 반환
     */
    Optional<RefreshToken> findByUserId(Long userId);

}