package com.example.sulsul.teacherprofile.service;

import com.example.sulsul.common.type.EType;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.teacherprofile.dto.request.TeacherProfileRequest;
import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import com.example.sulsul.teacherprofile.repository.TeacherProfileRepository;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeacherProfileService {

    private final TeacherProfileRepository teacherProfileRepository;

    public TeacherProfile getTeacherProfile(Long profileId){
        return teacherProfileRepository.findById(profileId).get();
    }

    public List<TeacherProfile> getProfileList(EType eType){
        return teacherProfileRepository.findByTeacher_EType(eType);
    }

    public void createTeacherProfile(User user, TeacherProfileRequest teacherProfileRequest){

        TeacherProfile teacherProfile = teacherProfileRepository.findByTeacher(user).get();
        teacherProfile.updateTeacherProfile(teacherProfileRequest);

        teacherProfileRepository.save(teacherProfile);
    }
}
