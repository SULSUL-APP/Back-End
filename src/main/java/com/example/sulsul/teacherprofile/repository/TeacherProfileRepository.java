package com.example.sulsul.teacherprofile.repository;

import com.example.sulsul.common.type.EType;
import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import com.example.sulsul.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {

    Optional<TeacherProfile> findByTeacher(User user);

    List<TeacherProfile> findByTeacher_EssayType(EType eType);

    // select * from teacherProfile order by created_at desc limit 5;
    List<TeacherProfile> findTop5ByOrderByCreatedDateDesc();
}
