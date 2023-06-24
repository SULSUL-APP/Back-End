package com.example.sulsul.user.repository;

import com.example.sulsul.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 이메일로 유저 찾기
     * @param email 유저 이메일
     * @return 이메일에 해당하는 유저 객체 반환
     */
    Optional<User> findByEmail(String email);
}