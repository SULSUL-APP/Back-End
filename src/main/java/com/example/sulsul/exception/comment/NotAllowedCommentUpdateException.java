package com.example.sulsul.exception.comment;

import com.example.sulsul.exception.AccessNotAllowedException;

public class NotAllowedCommentUpdateException extends AccessNotAllowedException {
    public NotAllowedCommentUpdateException() {
        super("COMMENT_04", "다른 유저가 작성한 댓글을 수정할 수 없습니다.");
    }
}