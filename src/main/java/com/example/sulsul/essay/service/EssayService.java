package com.example.sulsul.essay.service;

import com.example.sulsul.essay.dto.request.CreateEssayRequest;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.repository.EssayRepository;
import com.example.sulsul.exception.CustomException;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EssayService {

    private final UserRepository userRepository;
    private final EssayRepository essayRepository;

    public Essay createEssay(Long profileId, Long studentId, CreateEssayRequest request) {
        User teacher = userRepository.findById(profileId)
                .orElseThrow(() -> new CustomException("강사 유저를 찾을 수 없습니다."));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new CustomException("학생 유저를 찾을 수 없습니다."));

        Essay essay = request.toEntity(student, teacher);
        return essayRepository.save(essay);
    }
}