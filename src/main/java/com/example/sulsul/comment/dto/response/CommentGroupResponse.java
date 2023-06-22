package com.example.sulsul.comment.dto.response;

import com.example.sulsul.comment.entity.Comment;
import lombok.Getter;

import java.util.List;

@Getter
public class CommentGroupResponse {

    private List<CommentResponse> comments;

    public CommentGroupResponse(List<Comment> comments) {
        comments.stream()
                .map(CommentResponse::new)
                .forEach(comment -> this.comments.add(comment));
    }
}