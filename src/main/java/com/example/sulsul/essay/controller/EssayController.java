package com.example.sulsul.essay.controller;

import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.essay.dto.request.CreateEssayRequest;
import com.example.sulsul.essay.dto.request.RejectRequest;
import com.example.sulsul.essay.dto.response.*;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.service.EssayService;
import com.example.sulsul.exception.custom.CustomException;
import com.example.sulsul.exception.custom.CustomValidationException;
import com.example.sulsul.exceptionhandler.dto.response.ErrorResponse;
import com.example.sulsul.file.service.FileService;
import com.example.sulsul.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Essay", description = "첨삭 관련 API")
@RestController
@RequiredArgsConstructor
public class EssayController {

    private final EssayService essayService;
    private final FileService fileService;

    @Operation(summary = "첨삭요청 (학생)", description = "profileId에 해당하는 강사에게 첨삭을 요청한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RequestEssayResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/profiles/{profileId}/essay",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createEssay(@Parameter(description = "첨삭을 요청할 강사의 id")
                                         @PathVariable Long profileId,
                                         @ModelAttribute @Valid CreateEssayRequest request,
                                         BindingResult bindingResult) {
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

    @Operation(summary = "진행중인 첨삭에 첨삭파일 첨부 (강사)", description = "강사가 진행중인 첨삭에 첨삭파일을 첨부한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProceedEssayResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/essay/proceed/{essayId}/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadTeacherEssayFile(@Parameter(description = "파일을 첨부할 첨삭의 id")
                                                    @PathVariable Long essayId,
                                                    @Parameter(description = "첨부할 첨삭파일")
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

    @Operation(summary = "첨삭요청 목록 조회", description = "첨삭요청 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EssayGroupResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
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

    @Operation(summary = "진행중인 첨삭목록 조회", description = "진행중인 첨삭목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EssayGroupResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
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

    @Operation(summary = "거절된 첨삭목록 조회", description = "거절된 첨삭목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EssayGroupResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
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

    @Operation(summary = "완료된 첨삭목록 조회", description = "완료된 첨삭목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EssayGroupResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
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

    @Operation(summary = "첨삭요청 개별조회", description = "essayId에 해당하는 첨삭요청을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RequestEssayResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/essay/request/{essayId}")
    public ResponseEntity<?> getRequestEssay(@Parameter(description = "조회할 첨삭의 id")
                                             @PathVariable Long essayId) {
        RequestEssayResponse essayResponse =
                (RequestEssayResponse) essayService.getEssayResponseWithStudentFile(essayId);
        return new ResponseEntity<>(essayResponse, HttpStatus.OK);
    }

    @Operation(summary = "거절된 첨삭 개별조회", description = "거절된 첨삭을 개별조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RejectEssayResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/essay/reject/{essayId}")
    public ResponseEntity<?> getRejectEssay(@Parameter(description = "조회할 첨삭의 id")
                                            @PathVariable Long essayId) {
        RejectEssayResponse essayResponse =
                (RejectEssayResponse) essayService.getEssayResponseWithStudentFile(essayId);
        return new ResponseEntity<>(essayResponse, HttpStatus.OK);
    }

    @Operation(summary = "진행중인 첨삭 개별조회", description = "진행중인 첨삭을 개별조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProceedEssayResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/essay/proceed/{essayId}")
    public ResponseEntity<?> getProceedEssay(@Parameter(description = "조회할 첨삭의 id")
                                             @PathVariable Long essayId) {
        ProceedEssayResponse essayResponse =
                (ProceedEssayResponse) essayService.getEssayResponseWithFilePaths(essayId);
        return new ResponseEntity<>(essayResponse, HttpStatus.OK);
    }

    @Operation(summary = "완료된 첨삭 개별조회", description = "완료된 첨삭을 개별조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {CompleteEssayResponse.class, NotReviewedEssayResponse.class}))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/essay/complete/{essayId}")
    public ResponseEntity<?> getCompleteEssay(@Parameter(description = "조회할 첨삭의 id")
                                              @PathVariable Long essayId) {
        EssayResponse essayResponse = essayService.getEssayResponseWithFilePaths(essayId);
        boolean reviewed = essayService.checkEssayReviewState(essayId);
        if (reviewed) {
            return new ResponseEntity<>((CompleteEssayResponse) essayResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>((NotReviewedEssayResponse) essayResponse, HttpStatus.OK);
    }

    @Operation(summary = "첨삭요청 수락", description = "첨삭요청을 수락한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChangeEssayStateResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/essay/{essayId}/accept")
    public ResponseEntity<?> acceptEssay(@Parameter(description = "수락할 첨삭요청의 id")
                                         @PathVariable Long essayId) {
        Essay essay = essayService.acceptEssay(essayId);
        String message = "첨삭요청이 수락되었습니다.";

        // TODO: 첨삭요청 수락 알림 전송 로직
        return new ResponseEntity<>(new ChangeEssayStateResponse(message, essay), HttpStatus.OK);
    }

    @Operation(summary = "첨삭요청 거절", description = "첨삭요청을 거절한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChangeEssayStateResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/essay/{essayId}/reject", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> rejectEssay(@Parameter(description = "거절할 첨삭요청의 id")
                                         @PathVariable Long essayId,
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

    @Operation(summary = "진행중인 첨삭 완료", description = "진행중인 첨삭요청을 완료한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChangeEssayStateResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/essay/{essayId}/complete")
    public ResponseEntity<?> completeEssay(@Parameter(description = "완료할 첨삭의 id")
                                           @PathVariable Long essayId) {
        Essay essay = essayService.completeEssay(essayId);
        String message = "첨삭이 완료되었습니다.";

        // TODO: 첨삭완료 알림 전송 로직
        return new ResponseEntity<>(new ChangeEssayStateResponse(message, essay), HttpStatus.OK);
    }
}