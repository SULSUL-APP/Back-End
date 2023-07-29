package com.example.sulsul.exception.comment;

import com.example.sulsul.exception.BadInputException;

import java.util.Map;

public class InvalidCommentCreateException extends BadInputException {
    public InvalidCommentCreateException(Map<String, String> errors) {
        super("COMMENT_02", "댓글 생성에 실패했습니다.", errors);
    }
}