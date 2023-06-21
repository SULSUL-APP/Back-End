package com.example.sulsul.review.repository;

import com.example.sulsul.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 입력받은 아이디 값의 강사의 리뷰 목록을 출력한다.
     * @param teacherId 목록을 출력할 강사의 아이디 값
     * @return 강사의 리뷰 목록
     */
    @Query("select r from Review r where r.teacher.id = :teacherId")
    public List<Review> getReviewList(@Param("teacherId") Long teacherId);

}