package com.example.sulsul.exceptionhandler;

import com.example.sulsul.exception.CustomException;
import com.example.sulsul.exception.CustomValidationException;
import com.example.sulsul.exceptionhandler.dto.response.ErrorResponse;
import com.example.sulsul.exceptionhandler.dto.response.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> exceptionHandler(CustomException e) {
        return new ResponseEntity<>(new ErrorResponse(-1, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<?> validationApiExceptionHandler(CustomValidationException e) {
        return new ResponseEntity<>(new ValidationErrorResponse(-1, e.getMessage(), e.getErrorMap()), HttpStatus.BAD_REQUEST);
    }
}