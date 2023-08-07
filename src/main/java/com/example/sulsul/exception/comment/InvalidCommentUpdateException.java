package com.example.sulsul.exception.comment;

import com.example.sulsul.exception.BadInputException;

import java.util.Map;

public class InvalidCommentUpdateException extends BadInputException {
    public InvalidCommentUpdateException(Map<String, String> errors) {
        super("COMMENT_01", "댓글 수정에 실패했습니다.", errors);
    }
}