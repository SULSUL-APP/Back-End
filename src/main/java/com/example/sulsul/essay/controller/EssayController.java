package com.example.sulsul.essay.controller;

import com.example.sulsul.essay.dto.request.CreateEssayRequest;
import com.example.sulsul.essay.dto.response.CreateEssayResponse;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.service.EssayService;
import com.example.sulsul.exception.CustomException;
import com.example.sulsul.exception.CustomValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class EssayController {

    private final EssayService essayService;

    @PostMapping("/profiles/{profileId}/essay")
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

        long studentId = 1L; // 임시로 생성한 학생 id;

        Essay essay = essayService.createEssay(profileId, studentId, request);
        return new ResponseEntity<>(new CreateEssayResponse(essay), HttpStatus.CREATED);
    }
}