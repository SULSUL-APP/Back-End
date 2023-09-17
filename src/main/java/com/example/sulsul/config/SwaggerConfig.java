package com.example.sulsul.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SwaggerConfig {

    @Value("${domain.name}")
    private String host;

    @Bean
    public OpenAPI openAPI() {
        Server server = new Server().url(host);
        String key = "AccessToken";
        String refreshKey = "RefreshToken";

        SecurityRequirement requirement = new SecurityRequirement()
                .addList(key)
                .addList(refreshKey);

        SecurityScheme accessTokenSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);

        SecurityScheme refreshTokenSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("RefreshToken");

        Components components = new Components()
                .addSecuritySchemes(key, accessTokenSecurityScheme)
                .addSecuritySchemes(refreshKey, refreshTokenSecurityScheme);

        return new OpenAPI()
                .addServersItem(server)
                .info(new Info()
                        .title("SULSUL API Sever")
                        .description("SULSUL 프로젝트 API 명세서 입니다.")
                        .version("v1.0"))
                .addSecurityItem(requirement)
                .components(components);
    }
}