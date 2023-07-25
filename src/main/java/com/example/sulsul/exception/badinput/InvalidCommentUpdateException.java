package com.example.sulsul.exception.badinput;

import java.util.Map;

public class InvalidCommentUpdateException extends BadInputException {
    public InvalidCommentUpdateException(Map<String, String> errors) {
        super("201", "댓글 수정에 실패했습니다.", errors);
    }
}