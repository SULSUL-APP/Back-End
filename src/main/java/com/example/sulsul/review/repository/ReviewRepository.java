package com.example.sulsul.review.repository;

import com.example.sulsul.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    /**
     * 강사의 리뷰 목록을 조회한다.
     * @param teacherId 강사의 userId
     * @return 리뷰 목록을 반환한다.
     */
    List<Review> findAllByTeacherId(Long teacherId);

    /**
     * 첨삭에 작성된 리뷰를 조회한다.
     * @param essayId 조회할 첨삭 Id
     * @return 첨삭에 작성된 리뷰를 반환한다.
     */
    Optional<Review> findByEssayId(Long essayId);
}