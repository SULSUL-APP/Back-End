package com.example.sulsul.teacherprofile.service;

import com.example.sulsul.review.entity.Review;
import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import com.example.sulsul.teacherprofile.repository.TeacherProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeacherProfileService {

    private final TeacherProfileRepository teacherProfileRepository;

    public TeacherProfile getTeacherProfile(Long profileId) {
        return teacherProfileRepository.findById(profileId).get();
    }
}
