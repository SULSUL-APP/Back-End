package com.example.sulsul.config.jwt;

import com.example.sulsul.exception.AccessNotAllowedException;
import com.example.sulsul.exception.BaseException;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtExceptionHandler {

    public static void handle(HttpServletResponse response, AccessNotAllowedException exception) throws BaseException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.getWriter().print(exception.getMessage());
    }
}