package com.example.sulsul.essay.service;

import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.comment.repository.CommentRepository;
import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.common.type.ReviewState;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.essay.dto.request.CreateEssayRequest;
import com.example.sulsul.essay.dto.request.RejectRequest;
import com.example.sulsul.essay.dto.response.*;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.repository.EssayRepository;
import com.example.sulsul.exception.essay.EssayNotFoundException;
import com.example.sulsul.exception.file.FileNotFoundException;
import com.example.sulsul.exception.review.ReviewNotFoundException;
import com.example.sulsul.exception.user.TeacherNotFoundException;
import com.example.sulsul.file.entity.File;
import com.example.sulsul.file.repository.FileRepository;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.review.repository.ReviewRepository;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EssayService {

    private final UserRepository userRepository;
    private final EssayRepository essayRepository;
    private final FileRepository fileRepository;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public Essay createEssay(Long profileId, User student, CreateEssayRequest request) {
        // profileId에 해당하는 강사 유저 조회
        User teacher = userRepository.findById(profileId)
                .orElseThrow(() -> new TeacherNotFoundException(profileId));
        Essay essay = request.toEntity(student, teacher); // Essay 엔티티 생성
        return essayRepository.save(essay); // Essay 엔티티 저장
    }

    @Transactional(readOnly = true)
    public Essay getEssayById(Long essayId) {
        return essayRepository.findById(essayId)
                .orElseThrow(() -> new EssayNotFoundException(essayId));
    }

    @Transactional(readOnly = true)
    public List<Essay> getEssaysByUser(User user, EssayState essayState) {
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

    @Transactional(readOnly = true)
    public EssayResponse getEssayResponseWithStudentFile(Long essayId) {
        // essayId에 해당하는 첨삭 조회
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new EssayNotFoundException(essayId));

        Long studentId = essay.getStudent().getId();
        // 학생이 올린 첨삭파일 조회
        File file = fileRepository.getStudentEssayFile(essayId, studentId)
                .orElseThrow(() -> new FileNotFoundException());
        String filePath = file.getFilePath(); // 첨삭파일이 위치한 s3 경로
        // 첨삭요청 상태인 경우
        if (essay.getEssayState().equals(EssayState.REQUEST)) {
            return new RequestEssayResponse(essay, filePath);
        }
        // 첨삭거절 상태인 경우
        return new RejectEssayResponse(essay, filePath);
    }

    // TODO: 메소드가 너무 많은 역할을 하고 있음 - 리팩토링, 기능분리 필요
    // TODO: 테스트케이스 추가 - 강사가 첨삭파일을 업로드하지 않은 경우, 댓글이 작성되지 않은 경우 등
    @Transactional(readOnly = true)
    public EssayResponse getEssayResponseWithFilePaths(Long essayId) {
        // essayId에 해당하는 첨삭 조회
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new EssayNotFoundException(essayId));

        Long studentId = essay.getStudent().getId();
        // 학생이 올린 첨삭파일 조회
        File studentFile = fileRepository.getStudentEssayFile(essayId, studentId)
                .orElseThrow(() -> new FileNotFoundException());
        String studentFileFilePath = studentFile.getFilePath(); // 학생이 올린 첨삭파일의 s3 경로

        Long teacherId = essay.getTeacher().getId();
        // 강사가 올린 첨삭파일 조회
        Optional<File> teacherEssayFile = fileRepository.getTeacherEssayFile(essayId, teacherId);
        String teacherFileFilePath;
        if (teacherEssayFile.isEmpty()) {
            teacherFileFilePath = ""; // 강사가 아직 첨삭파일을 업로드하지 않은 경우
        } else {
            teacherFileFilePath = teacherEssayFile.get().getFilePath(); // 강사가 올린 첨삭파일의 s3 경로
        }
        // 첨삭에 작성된 모든 댓글 조회
        List<Comment> comments = commentRepository.findAllByEssayId(essayId);
//        if (comments == null) {
//            comments = new ArrayList<>(); // 아직 댓글이 없는 경우
//        }
        // 첨삭완료 상태인 경우
        if (essay.getEssayState().equals(EssayState.COMPLETE)) {
            // 리뷰가 작성된 경우
            if (essay.getReviewState().equals(ReviewState.ON)) {
                Review review = reviewRepository.findByEssayId(essayId)
                        .orElseThrow(() -> new ReviewNotFoundException(essayId));
                return new CompleteEssayResponse(essay, studentFileFilePath, teacherFileFilePath, comments, review);
            }
            // 리뷰가 작성되지 않은 경우
            return new NotReviewedEssayResponse(essay, studentFileFilePath, teacherFileFilePath, comments);
        }
        // 첨삭진행 상태인 경우
        return new ProceedEssayResponse(essay, studentFileFilePath, teacherFileFilePath, comments);
    }

    @Transactional
    public Essay acceptEssay(Long essayId) {
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new EssayNotFoundException(essayId));
        // 첨삭진행 상태로 변경
        essay.updateEssayState(EssayState.PROCEED);
        return essayRepository.save(essay);
    }

    @Transactional
    public Essay rejectEssay(Long essayId, RejectRequest rejectRequest) {
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new EssayNotFoundException(essayId));
        // 첨삭거절 상태로 변경
        essay.updateEssayState(EssayState.REJECT);
        essay.updateRejectDetail(rejectRequest.getRejectDetail());
        return essayRepository.save(essay);
    }

    @Transactional
    public Essay completeEssay(Long essayId) {
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new EssayNotFoundException(essayId));
        // 첨삭완료 상태로 변경
        essay.updateEssayState(EssayState.COMPLETE);
        return essayRepository.save(essay);
    }

    @Transactional(readOnly = true)
    public boolean checkEssayReviewState(Long essayId) {
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new EssayNotFoundException(essayId));
        ReviewState reviewState = essay.getReviewState();
        return reviewState.equals(ReviewState.ON);
    }
}