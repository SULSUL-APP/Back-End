package com.example.sulsul.exceptionhandler.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {
    private int errorCode;
    private String message;
    private Map<String, String> errorMap;
}