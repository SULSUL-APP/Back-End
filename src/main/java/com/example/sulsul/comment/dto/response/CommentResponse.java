package com.example.sulsul.comment.dto.response;

import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.user.entity.User;
import lombok.Getter;

@Getter
public class CommentResponse {

    private final Long commentId;
    private final String detail;

    // TODO: User -> UserDto
    private final User user;

    public CommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.detail = comment.getDetail();
        this.user = comment.getUser();
    }
}