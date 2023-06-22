package com.example.sulsul.essay.dto.response;

import com.example.sulsul.comment.dto.response.CommentGroupResponse;
import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.essay.entity.Essay;
import lombok.Getter;

import java.util.List;

@Getter
public class ProceedEssayResponse extends EssayResponse {

    private final String inquiry;
    private final String studentFilePath;
    private final String teacherFilePath;
    private final CommentGroupResponse comments;

    public ProceedEssayResponse(Essay essay, String studentFilePath,
                                String teacherFilePath, List<Comment> comments) {
        super(essay);
        this.inquiry = essay.getInquiry();
        this.studentFilePath = studentFilePath;
        this.teacherFilePath = teacherFilePath;
        this.comments = new CommentGroupResponse(comments);
    }
}