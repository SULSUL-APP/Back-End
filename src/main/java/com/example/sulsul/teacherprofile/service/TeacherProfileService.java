package com.example.sulsul.teacherprofile.service;

import com.example.sulsul.common.type.EType;
import com.example.sulsul.exception.essay.EssayNotFoundException;
import com.example.sulsul.exception.profile.ProfileNotFoundException;
import com.example.sulsul.exception.profile.TeacherProfileNotFoundException;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.teacherprofile.dto.request.TeacherProfileRequest;
import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import com.example.sulsul.teacherprofile.repository.TeacherProfileRepository;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherProfileService {

    private final TeacherProfileRepository teacherProfileRepository;

    public TeacherProfile getTeacherProfile(Long profileId){
        return teacherProfileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(profileId));
    }

    public TeacherProfile getTeacherProfile(User user){
        return teacherProfileRepository.findByTeacher(user)
                .orElseThrow(() -> new TeacherProfileNotFoundException(user.getId()));
    }

    public List<TeacherProfile> getProfileList(EType eType){
        List<TeacherProfile> Lists = teacherProfileRepository.findByTeacher_EssayType(eType);
        log.info("TeacherProfile Lists : {}", Lists);
        return Lists;
    }

    public void createTeacherProfile(User user, TeacherProfileRequest teacherProfileRequest){

        TeacherProfile teacherProfile = getTeacherProfile(user).updateTeacherProfile(teacherProfileRequest);
        teacherProfileRepository.save(teacherProfile);
    }

    public void regradeTeacherProfile(Integer score, User teacher){

        TeacherProfile teacherProfile = teacherProfileRepository.findByTeacher(teacher)
                .orElseThrow(()->new TeacherProfileNotFoundException(teacher.getId()));

        log.info("완료된 첨삭수 : {}", teacherProfile.getCompletedCount());
        Double sum = Double.parseDouble(teacherProfile.getReviewScore()) * teacherProfile.getCompletedCount() + score;
        teacherProfile.addCompletedCount();

        log.info("최신 반영 완료된 첨삭수 : {}", teacherProfile.getCompletedCount());

        Double Average = sum / teacherProfile.getCompletedCount();
        String reviewAverage = String.format("%.1f", Average);

        teacherProfile.updateReviewScore(reviewAverage);
        teacherProfileRepository.save(teacherProfile);
    }
}
