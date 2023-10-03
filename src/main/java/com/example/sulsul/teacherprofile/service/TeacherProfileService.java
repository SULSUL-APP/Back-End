package com.example.sulsul.teacherprofile.service;

import com.example.sulsul.common.type.EType;
import com.example.sulsul.exception.profile.ProfileNotFoundException;
import com.example.sulsul.exception.profile.TeacherProfileNotFoundException;
import com.example.sulsul.teacherprofile.dto.request.TeacherProfileRequest;
import com.example.sulsul.teacherprofile.dto.response.ProfileListResponse;
import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import com.example.sulsul.teacherprofile.repository.TeacherProfileRepository;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherProfileService {

    private final TeacherProfileRepository teacherProfileRepository;

    @Transactional(readOnly = true)
    public TeacherProfile getTeacherProfile(Long profileId) {
        return teacherProfileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(profileId));
    }

    @Transactional(readOnly = true)
    public TeacherProfile getTeacherProfile(User user) {
        return teacherProfileRepository.findByTeacher(user)
                .orElseThrow(() -> new TeacherProfileNotFoundException(user.getId()));
    }

    @Transactional(readOnly = true)
    public ProfileListResponse getProfileList(EType eType) {

        PageRequest page = PageRequest.of(0, 5);

        List<TeacherProfile> profileList = teacherProfileRepository.findByTeacher_EssayType(eType);
        List<TeacherProfile> newProfileList = teacherProfileRepository.findNewTeacherProfiles(eType, page);

        return new ProfileListResponse(profileList, newProfileList);
    }

    @Transactional
    public TeacherProfile updateTeacherProfile(User user, TeacherProfileRequest teacherProfileRequest) {

        TeacherProfile teacherProfile = getTeacherProfile(user).updateTeacherProfile(teacherProfileRequest);
        return teacherProfileRepository.save(teacherProfile);
    }

    @Transactional
    public void regradeTeacherProfile(Integer score, User teacher) {

        TeacherProfile teacherProfile = teacherProfileRepository.findByTeacher(teacher)
                .orElseThrow(() -> new TeacherProfileNotFoundException(teacher.getId()));

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