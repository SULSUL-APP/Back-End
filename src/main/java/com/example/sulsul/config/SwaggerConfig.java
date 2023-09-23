package com.example.sulsul.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomiser;
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

    @Bean
    public OpenApiCustomiser customOpenApiCustomiser() {
        return openApi -> {
            // 로그아웃 엔드포인트에 대한 명세 추가
            openApi.getPaths().addPathItem("/auth/logout", new PathItem()
                    .get(new Operation()
                            .summary("로그아웃")
                            .description("로그아웃 요청을 위해서는 AccessToken이 필요합니다.<br>" +
                                    "로그아웃시 서버에서는 RefreshToken을 제거합니다.<br>" +
                                    "AccessToken은 서버에서 만료시킬 수 없습니다.<br>" +
                                    "기존의 AccessToken으로 요청을 보내지 못하도록 로그인을 강제해야 합니다.")
                            .addTagsItem("Users")
                    )
            );
            // 카카오 소셜 로그인 엔드포인트에 대한 명세 추가
            openApi.getPaths().addPathItem("/oauth2/authorization/kakao", new PathItem()
                    .get(new Operation()
                            .summary("카카오 소셜로그인 (Swagger가 아닌 브라우저에서 url을 직접 입력후 요청)")
                            .description("Response Header에서 AccessToken과 RefreshToken을 확인할 수 있습니다.<br><br>" +
                                    "AccessToken: Authorization Header에 Bear {AccessToken} 형식으로 존재<br>" +
                                    "RefreshToken: RefreshToken Header에 {RefreshToken} 형식으로 존재<br><br>" +
                                    "AccessToken의 경우 요청시에도 똑같이 Authorization Header에 Bear {AccessToken} 형식으로 요청<br>" +
                                    "RefreshToken의 경우 요청시에도 똑같이 RefreshToken Header에 {RefreshToken} 형식으로 요청<br><br>" +
                                    "AccessToken은 1시간, RefreshToken은 2주간 유효합니다.<br>" +
                                    "AccessToken이 만료되면 GET /refresh을 RefreshToken과 함께 호출하여 AccessToken을 재발급 받을 수 있습니다.")
                            .addTagsItem("Users")
                    )
            );
        };
    }
}