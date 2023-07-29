package com.example.sulsul.fcm;

import com.example.sulsul.exception.fcm.FcmInitException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.*;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

@Configuration
public class FcmConfig {

    @Value("${fcm.key.path}")
    private String credential;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws FcmInitException {
        ClassPathResource resource = new ClassPathResource(credential);

        try (InputStream stream = resource.getInputStream()) {
            FirebaseApp firebaseApp = null;
            List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
            // 중복 초기화 방지
            if (firebaseApps != null && !firebaseApps.isEmpty()) {
                for (FirebaseApp app : firebaseApps) {
                    if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                        firebaseApp = app;
                    }
                }
            } else {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(stream))
                        .build();

                firebaseApp = FirebaseApp.initializeApp(options);
            }

            return FirebaseMessaging.getInstance(firebaseApp);
        } catch (Exception e) {
//            log.error("파이어베이스 이니셜라이저 오류 {}", e.getMessage());
            throw new FcmInitException();
        }

    }
}