package com.example.sulsul.fcm.repository;

import com.example.sulsul.fcm.entity.FcmToken;
import com.example.sulsul.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByUser(User user);
}