package com.example.sulsul.exceptionhandler;

import com.example.sulsul.exception.file.FileException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionHandler {


    /**
     * FileException 이 발생하면, ErrorResponse 와 HttpStatus.BAD_REQUEST 를 담은 ResponseEntity 반환한다.
     *
     * @param e FileException
     * @return ResponseEntity
     */
    @ExceptionHandler(FileException.class)
    public ResponseEntity<ErrorResponse> FileExceptionHandler(FileException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(e.getErrorCode().getCode())
                .messages(e.getMessage())
                .errors(e.getErrors())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


}
