package com.example.sulsul.comment.repository;

import com.example.sulsul.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * 해당 첨삭의 모든 댓글을 조회한다.
     * @param essayId 조회할 첨삭 id;
     * @return 해당 첨삭의 모든 댓글 반환
     */
    List<Comment> findAllByEssayId(Long essayId);

    // POST /essay/{essayId}/comments - save
    // PUT /comments/{commentId} - save
    // DELETE /commentss/{commentId} - deleteById
}