package com.example.sulsul.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${domain.name}")
    private String host;

    @Bean
    public OpenAPI openAPI() {
        Server server = new Server().url(host);
        return new OpenAPI()
                .addServersItem(server)
                .info(new Info()
                        .title("SULSUL API Sevrer")
                        .description("SULSUL 프로젝트 API 명세서 입니다.")
                        .version("v1.0"))
                .components(new Components().addSecuritySchemes("Bearer AccessToken",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("Bearer").bearerFormat("JWT"))
                );
    }
}