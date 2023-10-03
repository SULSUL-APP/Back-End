package com.example.sulsul.fcm.service;

import com.example.sulsul.fcm.entity.FcmToken;
import com.example.sulsul.fcm.repository.FcmTokenRepository;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void saveFcmToken(User user, String fcmToken) {
        Optional<FcmToken> optionalFcmToken = fcmTokenRepository.findByUser(user);
        // 저장된 fcmToken이 있는 경우
        if (optionalFcmToken.isPresent()) {
            FcmToken savedToken = optionalFcmToken.get();
            // 저장된 fcmToken과 전달된 fcmToken이 다른 경우 update
            if (!savedToken.getFcmToken().equals(fcmToken)) {
                savedToken.updateFcmToken(fcmToken);
                fcmTokenRepository.save(savedToken);
            }
            return;
        }
        // 저장된 fcmToken이 없는 경우
        FcmToken newToken = new FcmToken(user, fcmToken);
        fcmTokenRepository.save(newToken);
    }
}