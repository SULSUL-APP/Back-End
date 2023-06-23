package com.example.sulsul.essay.controller;

import com.example.sulsul.common.type.UType;
import com.example.sulsul.essay.dto.request.CreateEssayRequest;
import com.example.sulsul.essay.dto.request.RejectRequest;
import com.example.sulsul.essay.dto.response.*;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.entity.type.EssayState;
import com.example.sulsul.essay.service.EssayService;
import com.example.sulsul.essay.exception.CustomException;
import com.example.sulsul.essay.exception.CustomValidationException;
import com.example.sulsul.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class EssayController {

    private final EssayService essayService;

    /**
     * 첨삭요청
     */
    @PostMapping(value = "/profiles/{profileId}/essay",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createEssay(@PathVariable Long profileId,
                                         @Valid @RequestBody CreateEssayRequest request,
                                         BindingResult bindingResult) throws RuntimeException {

        // 첨삭 파일 여부 검증
        if (request.getFile().isEmpty()) {
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

        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // User student = (User) auth.getPrincipal();
        // 또는 @AuthenticationPrincipal 활용

        // 로그인 되어 있는 유저 객체를 가져오는 로직
        Long userId = 1L; // 임시로 생성한 유저 id;
        User loginedUser = User.builder()
                .id(userId)
                .build();

        if (loginedUser.getUType().equals(UType.TEACHER)) {
            throw new CustomException("강사는 첨삭요청을 보낼 수 없습니다.");
        }

        Essay essay = essayService.createEssay(profileId, loginedUser, request);
        return new ResponseEntity<>(new CreateEssayResponse(essay), HttpStatus.CREATED);
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
                .build();

        List<Essay> essays = essayService.getEssays(loginedUser, EssayState.REQUEST);
        return new ResponseEntity<>(new EssayGroupResponse(essays), HttpStatus.OK);
    }

    @GetMapping("/essay/proceed")
    public ResponseEntity<?> getProceedEssays() {

        // 로그인 되어 있는 유저 객체를 가져오는 로직
        Long userId = 1L; // 임시로 생성한 유저 id;
        User loginedUser = User.builder()
                .id(userId)
                .build();

        List<Essay> essays = essayService.getEssays(loginedUser, EssayState.PROCEED);
        return new ResponseEntity<>(new EssayGroupResponse(essays), HttpStatus.OK);
    }

    @GetMapping("/essay/reject")
    public ResponseEntity<?> getRejectEssays() {

        // 로그인 되어 있는 유저 객체를 가져오는 로직
        Long userId = 1L; // 임시로 생성한 유저 id;
        User loginedUser = User.builder()
                .id(userId)
                .build();

        List<Essay> essays = essayService.getEssays(loginedUser, EssayState.REJECT);
        return new ResponseEntity<>(new EssayGroupResponse(essays), HttpStatus.OK);
    }

    @GetMapping("/essay/complete")
    public ResponseEntity<?> getCompleteEssays() {

        // 로그인 되어 있는 유저 객체를 가져오는 로직
        Long userId = 1L; // 임시로 생성한 유저 id;
        User loginedUser = User.builder()
                .id(userId)
                .build();

        List<Essay> essays = essayService.getEssays(loginedUser, EssayState.COMPLETE);
        return new ResponseEntity<>(new EssayGroupResponse(essays), HttpStatus.OK);
    }

    /**
     * 첨삭개별 조회
     */
    @GetMapping("/essay/request/{essayId}")
    public ResponseEntity<?> getRequestEssay(@PathVariable Long essayId) {
        RequestEssayResponse essayResponse = (RequestEssayResponse) essayService.getEssayWithStudentFile(essayId);
        return new ResponseEntity<>(essayResponse, HttpStatus.OK);
    }

    @GetMapping("/essay/reject/{essayId}")
    public ResponseEntity<?> getRejectEssay(@PathVariable Long essayId) {
        RejectEssayResponse essayResponse = (RejectEssayResponse) essayService.getEssayWithStudentFile(essayId);
        return new ResponseEntity<>(essayResponse, HttpStatus.OK);
    }

    @GetMapping("/essay/proceed/{essayId}")
    public ResponseEntity<?> getProceedEssay(@PathVariable Long essayId) {
        ProceedEssayResponse essayResponse = (ProceedEssayResponse) essayService.getEssayWithFilePaths(essayId);
        return new ResponseEntity<>(essayResponse, HttpStatus.OK);
    }

    @GetMapping("/essay/complete/{essayId}")
    public ResponseEntity<?> getCompleteEssay(@PathVariable Long essayId) {
        CompleteEssayResponse essayResponse = (CompleteEssayResponse) essayService.getEssayWithFilePaths(essayId);
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