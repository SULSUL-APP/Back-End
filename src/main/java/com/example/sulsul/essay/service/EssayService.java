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
        UType userType = user.getUserType();
        Long userId = user.getId();
        if (userType.equals(UType.TEACHER)) {
            // 강사인 경우: 강사에게 요청된 첨삭글 목록 조회
            return essayRepository.findAllByTeacherIdAndEssayState(userId, essayState);
        }
        // 학생인 경우: 학생이 요청한 첨삭글 목록 조회
        return essayRepository.findAllByStudentIdAndEssayState(userId, essayState);
    }

    /**
     * 제거 예정
     */
    @Transactional(readOnly = true)
    public EssayResponse getEssayResponseWithStudentFile(Long essayId) {
        // essayId에 해당하는 첨삭 조회
        Essay essay = getEssayById(essayId);
        Long studentId = essay.getStudent().getId();
        // 학생이 올린 첨삭파일 조회
        String filePath = getStudentFilePath(essayId, studentId); // 첨삭파일이 위치한 s3 경로
        // 첨삭요청 상태인 경우
        if (essay.getEssayState().equals(EssayState.REQUEST)) {
            return new RequestEssayResponse(essay, filePath);
        }
        // 첨삭거절 상태인 경우
        return new RejectedEssayResponse(essay, filePath);
    }

    private String getStudentFilePath(Long essayId, Long studentId) {
        File file = fileRepository.getStudentEssayFile(essayId, studentId)
                .orElseThrow(() -> new FileNotFoundException());
        return file.getFilePath();
    }

    @Transactional(readOnly = true)
    public RequestEssayResponse getEssayRequest(Long essayId) {
        // essayId에 해당하는 첨삭 조회
        Essay essay = getEssayById(essayId);
        Long studentId = essay.getStudent().getId();
        // 학생이 올린 첨삭파일 조회
        String filePath = getStudentFilePath(essayId, studentId); // 첨삭파일이 위치한 s3 경로
        // 첨삭요청 정보와 파일경로 반환
        return new RequestEssayResponse(essay, filePath);
    }

    @Transactional(readOnly = true)
    public RejectedEssayResponse getEssayReject(Long essayId) {
        // essayId에 해당하는 첨삭 조회
        Essay essay = getEssayById(essayId);
        Long studentId = essay.getStudent().getId();
        // 학생이 올린 첨삭파일 조회
        String filePath = getStudentFilePath(essayId, studentId); // 첨삭파일이 위치한 s3 경로
        // 첨삭요청 정보와 파일경로 반환
        return new RejectedEssayResponse(essay, filePath);
    }

    /**
     * 제거 예정
     */
    @Transactional(readOnly = true)
    public EssayResponse getEssayResponseWithFilePaths(Long essayId) {
        // essayId에 해당하는 첨삭 조회
        Essay essay = essayRepository.findById(essayId)
                .orElseThrow(() -> new EssayNotFoundException(essayId));

        Long studentId = essay.getStudent().getId();
        // 학생이 올린 첨삭파일 조회
        String studentFilePath = getStudentFilePath(essayId, studentId); // 첨삭파일이 위치한 s3 경로 // 학생이 올린 첨삭파일의 s3 경로
        Long teacherId = essay.getTeacher().getId();
        // 강사가 올린 첨삭파일 조회
        Optional<File> teacherEssayFile = fileRepository.getTeacherEssayFile(essayId, teacherId);
        String teacherFilePath;
        if (teacherEssayFile.isEmpty()) {
            teacherFilePath = ""; // 강사가 아직 첨삭파일을 업로드하지 않은 경우
        } else {
            teacherFilePath = teacherEssayFile.get().getFilePath(); // 강사가 올린 첨삭파일의 s3 경로
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
                return new ReviewedEssayResponse(essay, studentFilePath, teacherFilePath, comments, review);
            }
            // 리뷰가 작성되지 않은 경우
            return new CompletedEssayResponse(essay, studentFilePath, teacherFilePath, comments);
        }
        // 첨삭진행 상태인 경우
        return new ProceedEssayResponse(essay, studentFilePath, teacherFilePath, comments);
    }

    private String getTeacherFilePath(Long essayId, Long teacherId) {
        Optional<File> teacherFile = fileRepository.getTeacherEssayFile(essayId, teacherId);
        String teacherFilePath = ""; // 강사가 아직 첨삭파일을 업로드하지 않은 경우
        if (teacherFile.isPresent()) { // 강사가 첨삭파일을 업로드한 경우
            teacherFilePath = teacherFile.get().getFilePath(); // 강사가 올린 첨삭파일의 s3 경로
        }
        return teacherFilePath;
    }

    @Transactional(readOnly = true)
    public ProceedEssayResponse getProceedEssay(Long essayId) {
        // essayId에 해당하는 첨삭 조회
        Essay essay = getEssayById(essayId);
        Long studentId = essay.getStudent().getId();
        // 학생이 올린 첨삭파일 조회
        String studentFilePath = getStudentFilePath(essayId, studentId); // 학생이 올린 첨삭파일의 s3 경로
        // 강사가 올린 첨삭파일 조회
        Long teacherId = essay.getTeacher().getId();
        String teacherFilePath = getTeacherFilePath(essayId, teacherId);
        // 첨삭에 작성된 모든 댓글 조회
        List<Comment> comments = commentRepository.findAllByEssayId(essayId);
        // 진행중인 첨삭 Response 반환
        return new ProceedEssayResponse(essay, studentFilePath, teacherFilePath, comments);
    }

    @Transactional(readOnly = true)
    public CompletedEssayResponse getCompleteEssay(Long essayId) {
        // essayId에 해당하는 첨삭 조회
        Essay essay = getEssayById(essayId);
        Long studentId = essay.getStudent().getId();
        // 학생이 올린 첨삭파일 조회
        String studentFilePath = getStudentFilePath(essayId, studentId); // 학생이 올린 첨삭파일의 s3 경로
        // 강사가 올린 첨삭파일 조회
        Long teacherId = essay.getTeacher().getId();
        String teacherFilePath = getTeacherFilePath(essayId, teacherId);
        // 첨삭에 작성된 모든 댓글 조회
        List<Comment> comments = commentRepository.findAllByEssayId(essayId);
        // 리뷰가 작성되지 않은 경우
        return new CompletedEssayResponse(essay, studentFilePath, teacherFilePath, comments);
    }

    @Transactional(readOnly = true)
    public ReviewedEssayResponse getReviewedEssay(Long essayId) {
        // essayId에 해당하는 첨삭 조회
        Essay essay = getEssayById(essayId);
        Long studentId = essay.getStudent().getId();
        // 학생이 올린 첨삭파일 조회
        String studentFilePath = getStudentFilePath(essayId, studentId); // 학생이 올린 첨삭파일의 s3 경로
        // 강사가 올린 첨삭파일 조회
        Long teacherId = essay.getTeacher().getId();
        String teacherFilePath = getTeacherFilePath(essayId, teacherId);
        // 첨삭에 작성된 모든 댓글 조회
        List<Comment> comments = commentRepository.findAllByEssayId(essayId);
        // 첨삭에 작성된 리뷰 조회
        Review review = reviewRepository.findByEssayId(essayId)
                .orElseThrow(() -> new ReviewNotFoundException(essayId));
        return new ReviewedEssayResponse(essay, studentFilePath, teacherFilePath, comments, review);
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

    /**
     * 제거 예정
     */
    @Transactional(readOnly = true)
    public boolean checkEssayReviewState(Long essayId) {
        Essay essay = getEssayById(essayId);
        ReviewState reviewState = essay.getReviewState();
        return reviewState.equals(ReviewState.ON);
    }
}