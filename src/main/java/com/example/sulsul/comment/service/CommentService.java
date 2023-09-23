package com.example.sulsul.comment.service;

import com.example.sulsul.comment.dto.request.CommentRequest;
import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.comment.repository.CommentRepository;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.repository.EssayRepository;
import com.example.sulsul.exception.comment.CommentNotFoundException;
import com.example.sulsul.exception.essay.EssayNotFoundException;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final EssayRepository essayRepository;
    private final CommentRepository commentRepository;

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }

    public List<Comment> getComments(Long essayId) {
        return commentRepository.findAllByEssayId(essayId);
    }

    public Comment createComment(Long essayId, User user, CommentRequest request) {
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new EssayNotFoundException(essayId));

        Comment comment = Comment.builder()
                .essay(essay)
                .user(user)
                .detail(request.getDetail())
                .build();

        return commentRepository.save(comment);
    }

    public Comment updateComment(Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        comment.updateDetail(request.getDetail());
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        commentRepository.deleteById(comment.getId());
    }
}