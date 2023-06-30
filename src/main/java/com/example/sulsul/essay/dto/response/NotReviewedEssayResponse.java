package com.example.sulsul.essay.dto.response;

import com.example.sulsul.comment.dto.response.CommentResponse;
import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.essay.entity.Essay;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class NotReviewedEssayResponse extends EssayResponse {

    private final String inquiry;
    private final String studentFilePath;
    private final String teacherFilePath;
    private final List<CommentResponse> comments = new ArrayList<>();

    public NotReviewedEssayResponse(Essay essay, String studentFilePath,
                                    String teacherFilePath, List<Comment> comments) {
        super(essay);
        this.inquiry = essay.getInquiry();
        this.studentFilePath = studentFilePath;
        this.teacherFilePath = teacherFilePath;
        comments.stream()
                .map(CommentResponse::new)
                .forEach(comment -> this.comments.add(comment));
    }
}