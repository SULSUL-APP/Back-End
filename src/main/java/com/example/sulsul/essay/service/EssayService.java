package com.example.sulsul.essay.service;

import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.comment.repository.CommentRepository;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.essay.dto.request.CreateEssayRequest;
import com.example.sulsul.essay.dto.request.RejectRequest;
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
        // profileId에 해당하는 강사 유저 조회
        User teacher = userRepository.findById(profileId)
                .orElseThrow(() -> new CustomException("강사 유저를 찾을 수 없습니다."));

        // 첨삭파일 저장 로직

        Essay essay = request.toEntity(student, teacher); // Essay 엔티티 생성
        return essayRepository.save(essay); // Essay 엔티티 저장
    }

    public List<Essay> getEssays(User user, EssayState essayState) {
        UType userType = user.getUType();
        Long userId = user.getId();
        List<Essay> essays = null;

        if (userType.equals(UType.TEACHER)) { // 강사인 경우
            // 강사에게 요청된 첨삭글 목록 조회
            essays = essayRepository.findAllByTeacherIdAndEssayState(userId, essayState);
        } else if (userType.equals(UType.STUDENT)) { // 학생인 경우
            // 학생이 요청한 첨삭글 목록 조회
            essays = essayRepository.findAllByStudentIdAndEssayState(userId, essayState);
        }
        return essays; // 첨삭목록 반환
    }

    public EssayResponse getEssayWithStudentFile(Long essayId) {
        // essayId에 해당하는 첨삭 조회
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new CustomException("해당 첨삭글을 찾을 수 없습니다."));

        Long studentId = essay.getStudent().getId();
        // 학생이 올린 첨삭파일 조회
        File file = fileRepository.getStudentEssayFile(essayId, studentId)
                .orElseThrow(() -> new CustomException("해당 첨삭글의 첨삭파일을 찾을 수 없습니다."));
        String filePath = file.getFilePath(); // 첨삭파일이 위치한 s3 경로

        // 첨삭요청 상태인 경우
        if (essay.getEssayState().equals(EssayState.REQUEST)) {
            return new RequestEssayResponse(essay, filePath);
        }

        // 첨삭거절 상태인 경우
        return new RejectEssayResponse(essay, filePath);
    }

    public EssayResponse getEssayWithFilePaths(Long essayId) {
        // essayId에 해당하는 첨삭 조회
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new CustomException("해당 첨삭글을 찾을 수 없습니다."));

        Long studentId = essay.getStudent().getId();
        // 학생이 올린 첨삭파일 조회
        File studentFile = fileRepository.getStudentEssayFile(essayId, studentId)
                .orElseThrow(() -> new CustomException("해당 첨삭글의 첨삭파일을 찾을 수 없습니다."));
        String studentFileFilePath = studentFile.getFilePath(); // 학생이 올린 첨삭파일의 s3 경로

        Long teacherId = essay.getTeacher().getId();
        // 강사가 올린 첨삭파일 조회
        File teacherFile = fileRepository.getTeacherEssayFile(essayId, teacherId)
                .orElseThrow(() -> new CustomException("해당 첨삭글의 첨삭파일을 찾을 수 없습니다."));
        String teacherFileFilePath = teacherFile.getFilePath(); // 강사가 올린 첨삭파일의 s3 경로

        // 첨삭에 작성된 모든 댓글 조회
        List<Comment> comments = commentRepository.findAllByEssayId(essayId);

        // 첨삭완료 상태인 경우 && 리뷰가 작성된 경우
        if (essay.getEssayState().equals(EssayState.COMPLETE)) {
            if (essay.getReviewState().equals(ReviewState.ON)) {
                Review review = reviewRepository.findByEssayId(essayId)
                        .orElseThrow(() -> new CustomException("해당 첨삭글의 리뷰를 찾을 수 없습니다."));
                return new CompleteEssayResponse(essay, studentFileFilePath, teacherFileFilePath, comments, review);
            }
        }
        // 첨삭진행 상태인 경우 || 첨삭이 완료되었지만 리뷰되지 않은 경우
        return new ProceedEssayResponse(essay, studentFileFilePath, teacherFileFilePath, comments);
    }

    public Essay acceptEssay(Long essayId) {
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new CustomException("해당 첨삭글을 찾을 수 없습니다."));

        essay.updateEssayState(EssayState.PROCEED);
        return essayRepository.save(essay);
    }

    public Essay rejectEssay(Long essayId, RejectRequest rejectRequest) {
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new CustomException("해당 첨삭글을 찾을 수 없습니다."));

        essay.updateEssayState(EssayState.REJECT);
        essay.updateRejectDetail(rejectRequest.getRejectDetail());
        return essayRepository.save(essay);
    }

    public Essay completeEssay(Long essayId) {
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new CustomException("해당 첨삭글을 찾을 수 없습니다."));

        essay.updateEssayState(EssayState.COMPLETE);
        return essayRepository.save(essay);
    }
}