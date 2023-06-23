package com.example.sulsul.comment.dto.response;

import lombok.Getter;

@Getter
public class DeleteSuccessResponse {

    String message;

    public DeleteSuccessResponse(String message) {
        this.message = message;
    }
}