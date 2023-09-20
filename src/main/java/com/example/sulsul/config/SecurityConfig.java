package com.example.sulsul.config;

import com.example.sulsul.config.jwt.JwtAuthenticationFilter;
import com.example.sulsul.config.oauth.CustomOAuth2UserService;
import com.example.sulsul.config.oauth.OAuth2AuthenticationFailureHandler;
import com.example.sulsul.config.oauth.OAuth2AuthenticationSuccessHandler;
import com.example.sulsul.config.security.CustomAccessDeniedHandler;
import com.example.sulsul.config.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private static final String[] SWAGGER_URL = {
            "/api-docs",
            "/v3/api-docs/**",
            "/swagger-*/**",
            "/webjars/**"
    };

    private final String[] GET_PERMIT_API_URL = {
            "/",
            "/refresh",
    };

    private final String[] POST_PERMIT_API_URL = {
            "/refresh",
    };

    private final LogoutHandler logoutService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .cors().configurationSource(corsConfigurationSource())

                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .csrf().disable()
                .headers().frameOptions().disable()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers(SWAGGER_URL).permitAll()
                .antMatchers(HttpMethod.GET, GET_PERMIT_API_URL).permitAll()
//                .antMatchers(HttpMethod.POST, POST_PERMIT_API_URL).permitAll()
                .anyRequest().authenticated()

                .and()
                .logout(logoutConfig -> {
                    logoutConfig.logoutUrl("/auth/logout")
                            .addLogoutHandler(logoutService)
                            .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
                })

                .exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())

                .and()
                .oauth2Login()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .userInfoEndpoint().userService(customOAuth2UserService)
                .and()

                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setExposedHeaders(List.of("*"));

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}