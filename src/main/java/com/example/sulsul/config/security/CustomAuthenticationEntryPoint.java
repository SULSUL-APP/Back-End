package com.example.sulsul.config.security;

import com.example.sulsul.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
인증이 실패했을 때 예외를 처리하는 클래스
 */
@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        log.info("[commence] 인증 실패로 response.sendError 발생");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().print(new UserNotFoundException());

        // dto 전송방식이 아니어도 바로 error를 전송하는 방식도 가능함
        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED);

    }

}
