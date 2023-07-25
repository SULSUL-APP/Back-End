package com.example.sulsul.exception.notfound;

import java.util.Map;

public class CommentNotFoundException extends ResourceNotFoundException {
    public CommentNotFoundException(long commentId) {
        super("204", "해당 댓글을 찾을 수 없습니다.", Map.of("commentId", String.valueOf(commentId)));
    }
}