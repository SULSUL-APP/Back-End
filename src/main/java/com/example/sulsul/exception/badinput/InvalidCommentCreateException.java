package com.example.sulsul.exception.badinput;

import java.util.Map;

public class InvalidCommentCreateException extends BadInputException {
    public InvalidCommentCreateException(Map<String, String> errors) {
        super("200", "댓글 생성에 실패했습니다.", errors);
    }
}