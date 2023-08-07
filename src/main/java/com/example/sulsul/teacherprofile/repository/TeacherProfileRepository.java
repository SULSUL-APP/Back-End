package com.example.sulsul.teacherprofile.repository;

import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {

    Optional<TeacherProfile> findById(Long teacherProfileId);
}
