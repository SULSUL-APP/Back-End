package com.example.sulsul.essay.service;

import com.example.sulsul.common.type.UType;
import com.example.sulsul.essay.dto.request.CreateEssayRequest;
import com.example.sulsul.essay.dto.response.EssayResponse;
import com.example.sulsul.essay.dto.response.RejectEssayResponse;
import com.example.sulsul.essay.dto.response.RequestEssayResponse;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.entity.type.EssayState;
import com.example.sulsul.essay.repository.EssayRepository;
import com.example.sulsul.essay.exception.CustomException;
import com.example.sulsul.file.entity.File;
import com.example.sulsul.file.repository.FileRepository;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EssayService {

    private final UserRepository userRepository;
    private final EssayRepository essayRepository;
    private final FileRepository fileRepository;

    public Essay createEssay(Long profileId, User student, CreateEssayRequest request) {
        User teacher = userRepository.findById(profileId)
                .orElseThrow(() -> new CustomException("강사 유저를 찾을 수 없습니다."));

        // 첨삭파일 저장 로직

        Essay essay = request.toEntity(student, teacher);
        return essayRepository.save(essay);
    }

    public List<Essay> getEssays(User user, EssayState essayState) {
        UType userType = user.getUType();
        Long userId = user.getId();
        List<Essay> essays = null;

        if (userType.equals(UType.TEACHER)) {
            essays = essayRepository.findAllByTeacherAndEssayState(userId, essayState);
        } else if (userType.equals(UType.STUDENT)) {
            essays = essayRepository.findAllByStudentAndEssayState(userId, essayState);
        }
        return essays;
    }

    public EssayResponse getEssayWithStudentFile(Long essayId) {
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new CustomException("해당 첨삭글을 찾을 수 없습니다."));

        Long studentId = essay.getStudent().getId();
        File file = fileRepository.getStudentEssayFile(essayId, studentId)
                .orElseThrow(() -> new CustomException("해당 첨삭글의 첨삭파일을 찾을 수 없습니다."));
        String filePath = file.getFilePath();

        if (essay.getEssayState().equals(EssayState.REQUEST)) {
            return new RequestEssayResponse(essay, filePath);
        }

        return new RejectEssayResponse(essay, filePath); // REJECT
    }

    public Object getEssayWithFilePaths(Long essayId) {
        return null;
    }
}