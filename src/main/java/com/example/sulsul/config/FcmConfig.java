package com.example.sulsul.config;

import com.example.sulsul.exception.fcm.FcmInitException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class FcmConfig {

    @Value("${fcm.key.path}")
    private String credential;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws FcmInitException {
        ClassPathResource resource = new ClassPathResource(credential);
        System.out.println("resource = " + resource.getPath());
        System.out.println("resource = " + resource.getFilename());

        try (InputStream stream = resource.getInputStream()) {
            System.out.println("try문 시작");
            FirebaseApp firebaseApp = null;
            List<FirebaseApp> firebaseApps = FirebaseApp.getApps();

            Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append("===========line: ");
                sb.append(line);
                sb.append('\n');
            }
            System.out.println(sb);

            // 중복 초기화 방지
            if (firebaseApps != null && !firebaseApps.isEmpty()) {

                for (FirebaseApp app : firebaseApps) {
                    if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                        firebaseApp = app;
                    }
                }
            } else {

                System.out.println("else문 시작");

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(stream))
                        .build();

                String projectId = options.getProjectId();
                System.out.println("======projectId = " + projectId);

                firebaseApp = FirebaseApp.initializeApp(options);
            }

            System.out.println("=========firebaseApp = " + firebaseApp);
            return FirebaseMessaging.getInstance(firebaseApp);

        } catch (Exception e) {
            String message = e.getMessage();
            System.out.println("==========message = " + message);
            throw new FcmInitException();
        }
    }
}