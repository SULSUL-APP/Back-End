package com.example.sulsul.teacherprofile.repository;

import com.example.sulsul.common.type.EType;
import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import com.example.sulsul.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {

    Optional<TeacherProfile> findById(Long teacherProfileId);

    Optional<TeacherProfile> findByTeacher(User user);

    List<TeacherProfile> findByTeacher_EType(EType eType);
}
