package com.example.sulsul.essay.repository;

import com.example.sulsul.common.type.EType;
import com.example.sulsul.essay.entity.Essay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EssayRepository extends JpaRepository<Essay, Long> {

    // POST /essay
    // POST /profiles/{profileId}/essay - save

    // GET /essay/request - findByETypeAndTeacher / findByETypeAndStudent
    // GET /essay/proceed
    // GET /essay/reject
    // GET /essay/complete
    List<Essay> findAllByETypeAndTeacher(EType essayType, Long teacherId);
    List<Essay> findAllByETypeAndStudent(EType essayType, Long studentId);

    // GET /essay/request/{essayId} - findEssayById
    // GET /essay/request/{essayId}
    // GET /essay/request/{essayId}
    // GET /essay/request/{essayId}
    Optional<Essay> findEssayById(Long essayId);

    // PUT /essay/{essayId}/accept - save
    // PUT /essay/{essayId}/reject
    // PUT /essay/{essayId}/complete
}