package com.example.sulsul.comment.dto.response;

import com.example.sulsul.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentGroupResponse {

    @Schema(description = "댓글 리스트")
    private final List<CommentResponse> comments = new ArrayList<>();

    public CommentGroupResponse(List<Comment> comments) {
        comments.stream()
                .map(CommentResponse::new)
                .forEach(comment -> this.comments.add(comment));
    }
}