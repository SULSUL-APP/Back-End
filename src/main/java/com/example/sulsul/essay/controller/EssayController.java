package com.example.sulsul.essay.controller;

import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.essay.dto.request.CreateEssayRequest;
import com.example.sulsul.essay.dto.request.RejectRequest;
import com.example.sulsul.essay.dto.response.*;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.service.EssayService;
import com.example.sulsul.exception.CustomException;
import com.example.sulsul.exception.CustomValidationException;
import com.example.sulsul.file.service.FileService;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EssayController {

    private final EssayService essayService;
    private final FileService fileService;

    /**
     * 첨삭요청 (학생)
     */
    @PostMapping(value = "/profiles/{profileId}/essay",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createEssay(@PathVariable Long profileId,
                                         @ModelAttribute @Valid CreateEssayRequest request,
                                         BindingResult bindingResult) throws RuntimeException {
        // 첨삭 파일 여부 검증
        if (request.getEssayFile().isEmpty()) {
            throw new CustomException("첨삭파일이 첨부되지 않았습니다.");
        }
        // 입력값 유효성 검사
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            throw new CustomValidationException("입력값 유효성 검사 실패", errorMap);
        }
        // 로그인 되어 있는 유저 객체를 가져오는 로직
        Long userId = 2L; // 임시로 생성한 유저 id;
        User loginedUser = User.builder()
                .id(userId)
                .uType(UType.STUDENT)
                .build();
        // 학생 유저만 첨삭요청 가능
        if (loginedUser.getUType().equals(UType.TEACHER)) {
            throw new CustomException("강사는 첨삭요청을 보낼 수 없습니다.");
        }
        // 첨삭 엔티티 생성
        Essay essay = essayService.createEssay(profileId, loginedUser, request);
        // 첨삭 파일 업로드
        fileService.uploadEssayFile(loginedUser, essay, request.getEssayFile());
        // 첨삭요청 응답 생성
        RequestEssayResponse essayResponse =
                (RequestEssayResponse) essayService.getEssayResponseWithStudentFile(essay.getId());
        // 첨삭 요청 완료: 201 CREATED
        return new ResponseEntity<>(essayResponse, HttpStatus.CREATED);
    }

    /**
     * 진행중인 첨삭에 첨삭파일 첨부 (강사)
     */
    @PostMapping(value = "/essay/proceed/{essayId}/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadTeacherEssayFile(@PathVariable Long essayId,
                                                    @RequestParam("essayFile") MultipartFile essayFile) {
        // 첨삭 파일 여부 검증
        if (essayFile.isEmpty()) {
            throw new CustomException("첨삭파일이 첨부되지 않았습니다.");
        }
        // 로그인 되어 있는 유저 객체를 가져오는 로직
        Long userId = 1L; // 임시로 생성한 유저 id;
        User loginedUser = User.builder()
                .id(userId)
                .uType(UType.TEACHER)
                .build();
        // 첨삭 엔티티 조회
        Essay essay = essayService.getEssayById(essayId);
        // 강사 첨삭 파일 업로드
        fileService.uploadEssayFile(loginedUser, essay, essayFile);
        ProceedEssayResponse essayResponse =
                (ProceedEssayResponse) essayService.getEssayResponseWithFilePaths(essayId);
        // 강사 첨삭 파일 업로드 완료: 201 CREATED
        return new ResponseEntity<>(essayResponse, HttpStatus.CREATED);
    }

    /**
     * 첨삭목록 조회
     */
    @GetMapping("/essay/request")
    public ResponseEntity<?> getRequestEssays() {
        // 로그인 되어 있는 유저 객체를 가져오는 로직
        Long userId = 1L; // 임시로 생성한 유저 id;
        User loginedUser = User.builder()
                .id(userId)
                .uType(UType.STUDENT)
                .build();
        // 첨삭요청 목록 조회
        List<Essay> essays = essayService.getEssaysByUser(loginedUser, EssayState.REQUEST);
        return new ResponseEntity<>(new EssayGroupResponse(essays), HttpStatus.OK);
    }

    @GetMapping("/essay/proceed")
    public ResponseEntity<?> getProceedEssays() {
        // 로그인 되어 있는 유저 객체를 가져오는 로직
        Long userId = 1L; // 임시로 생성한 유저 id;
        User loginedUser = User.builder()
                .id(userId)
                .build();
        // 진행중인 첨삭목록 조회
        List<Essay> essays = essayService.getEssaysByUser(loginedUser, EssayState.PROCEED);
        return new ResponseEntity<>(new EssayGroupResponse(essays), HttpStatus.OK);
    }

    @GetMapping("/essay/reject")
    public ResponseEntity<?> getRejectEssays() {
        // 로그인 되어 있는 유저 객체를 가져오는 로직
        Long userId = 1L; // 임시로 생성한 유저 id;
        User loginedUser = User.builder()
                .id(userId)
                .build();
        // 거절된 첨삭목록 조회
        List<Essay> essays = essayService.getEssaysByUser(loginedUser, EssayState.REJECT);
        return new ResponseEntity<>(new EssayGroupResponse(essays), HttpStatus.OK);
    }

    @GetMapping("/essay/complete")
    public ResponseEntity<?> getCompleteEssays() {
        // 로그인 되어 있는 유저 객체를 가져오는 로직
        Long userId = 1L; // 임시로 생성한 유저 id;
        User loginedUser = User.builder()
                .id(userId)
                .build();
        // 완료된 첨삭목록 조회
        List<Essay> essays = essayService.getEssaysByUser(loginedUser, EssayState.COMPLETE);
        return new ResponseEntity<>(new EssayGroupResponse(essays), HttpStatus.OK);
    }

    /**
     * 첨삭개별 조회
     */
    @GetMapping("/essay/request/{essayId}")
    public ResponseEntity<?> getRequestEssay(@PathVariable Long essayId) {
        RequestEssayResponse essayResponse =
                (RequestEssayResponse) essayService.getEssayResponseWithStudentFile(essayId);
        return new ResponseEntity<>(essayResponse, HttpStatus.OK);
    }

    @GetMapping("/essay/reject/{essayId}")
    public ResponseEntity<?> getRejectEssay(@PathVariable Long essayId) {
        RejectEssayResponse essayResponse =
                (RejectEssayResponse) essayService.getEssayResponseWithStudentFile(essayId);
        return new ResponseEntity<>(essayResponse, HttpStatus.OK);
    }

    @GetMapping("/essay/proceed/{essayId}")
    public ResponseEntity<?> getProceedEssay(@PathVariable Long essayId) {
        ProceedEssayResponse essayResponse =
                (ProceedEssayResponse) essayService.getEssayResponseWithFilePaths(essayId);
        return new ResponseEntity<>(essayResponse, HttpStatus.OK);
    }

    @GetMapping("/essay/complete/{essayId}")
    public ResponseEntity<?> getCompleteEssay(@PathVariable Long essayId) {
        CompleteEssayResponse essayResponse =
                (CompleteEssayResponse) essayService.getEssayResponseWithFilePaths(essayId);
        return new ResponseEntity<>(essayResponse, HttpStatus.OK);
    }

    /**
     * 첨삭상태 변경
     */
    @PutMapping("/essay/{essayId}/accept")
    public ResponseEntity<?> acceptEssay(@PathVariable Long essayId) {
        Essay essay = essayService.acceptEssay(essayId);
        String message = "첨삭요청이 수락되었습니다.";

        // TODO: 첨삭요청 수락 알림 전송 로직
        return new ResponseEntity<>(new ChangeEssayStateResponse(message, essay), HttpStatus.OK);
    }

    @PutMapping("/essay/{essayId}/reject")
    public ResponseEntity<?> rejectEssay(@PathVariable Long essayId,
                                         @Valid @RequestBody RejectRequest rejectRequest,
                                         BindingResult bindingResult) {
        // 거절사유 유효성 검사
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError("rejectDetail");
            String message = fieldError.getDefaultMessage();
            throw new CustomException(message);
        }

        Essay essay = essayService.rejectEssay(essayId, rejectRequest);
        String message = "첨삭요청이 거절되었습니다.";

        // TODO: 첨삭요청 거절 알림 전송 로직
        return new ResponseEntity<>(new ChangeEssayStateResponse(message, essay), HttpStatus.OK);
    }

    @PutMapping("/essay/{essayId}/complete")
    public ResponseEntity<?> completeEssay(@PathVariable Long essayId) {
        Essay essay = essayService.completeEssay(essayId);
        String message = "첨삭이 완료되었습니다.";

        // TODO: 첨삭완료 알림 전송 로직
        return new ResponseEntity<>(new ChangeEssayStateResponse(message, essay), HttpStatus.OK);
    }
}