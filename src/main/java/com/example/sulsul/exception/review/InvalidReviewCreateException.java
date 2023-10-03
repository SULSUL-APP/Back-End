package com.example.sulsul.exception.review;

import com.example.sulsul.exception.BadInputException;

import java.util.Map;

public class InvalidReviewCreateException extends BadInputException {
    public InvalidReviewCreateException(Map<String, String> errors) {
        super("REVIEW_01", "리뷰 작성에 실패했습니다.", errors);
    }
}