package com.example.sulsul.essay.service;

import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.comment.repository.CommentRepository;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.essay.dto.request.CreateEssayRequest;
import com.example.sulsul.essay.dto.response.*;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.entity.type.EssayState;
import com.example.sulsul.essay.entity.type.ReviewState;
import com.example.sulsul.essay.repository.EssayRepository;
import com.example.sulsul.essay.exception.CustomException;
import com.example.sulsul.file.entity.File;
import com.example.sulsul.file.repository.FileRepository;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.review.repository.ReviewRepository;
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
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

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
            essays = essayRepository.findAllByTeacherIdAndEssayState(userId, essayState);
        } else if (userType.equals(UType.STUDENT)) {
            essays = essayRepository.findAllByStudentIdAndEssayState(userId, essayState);
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

    public EssayResponse getEssayWithFilePaths(Long essayId) {

        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new CustomException("해당 첨삭글을 찾을 수 없습니다."));

        // 학생이 올린 첨삭파일 조회
        Long studentId = essay.getStudent().getId();
        File studentFile = fileRepository.getStudentEssayFile(essayId, studentId)
                .orElseThrow(() -> new CustomException("해당 첨삭글의 첨삭파일을 찾을 수 없습니다."));
        String studentFileFilePath = studentFile.getFilePath();

        // 강사가 올린 첨삭파일 조회
        Long teacherId = essay.getTeacher().getId();
        File teacherFile = fileRepository.getTeacherEssayFile(essayId, teacherId)
                .orElseThrow(() -> new CustomException("해당 첨삭글의 첨삭파일을 찾을 수 없습니다."));
        String teacherFileFilePath = teacherFile.getFilePath();

        // 첨삭에 작성된 모든 댓글 조회
        List<Comment> comments = commentRepository.findAllByEssayId(essayId);

        // 첨삭완료 상태인 경우
        if (essay.getEssayState().equals(EssayState.COMPLETE)) {
            Review review = null;
            if (essay.getReviewState().equals(ReviewState.ON)) {
                review = reviewRepository.findByEssayId(essayId)
                        .orElseThrow(() -> new CustomException("해당 첨삭글의 리뷰를 찾을 수 없습니다."));
            }
            return new CompleteEssayResponse(essay, studentFileFilePath, teacherFileFilePath, comments, review);
        }
        // 첨삭진행 상태인 경우
        return new ProceedEssayResponse(essay, studentFileFilePath, teacherFileFilePath, comments);
    }
}