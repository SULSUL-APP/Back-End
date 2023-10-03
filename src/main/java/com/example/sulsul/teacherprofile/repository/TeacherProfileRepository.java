package com.example.sulsul.teacherprofile.repository;

import com.example.sulsul.common.type.EType;
import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import com.example.sulsul.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {

    Optional<TeacherProfile> findByTeacher(User user);

    List<TeacherProfile> findByTeacher_EssayType(EType eType);

    @Query("select tp from TeacherProfile tp where tp.teacher.essayType = :essayType order by tp.createdDate desc")
    List<TeacherProfile> findNewTeacherProfiles(@Param("essayType") EType essayType, Pageable pageable);
}
