package com.example.sulsul.essay.controller;

import com.example.sulsul.common.CurrentUser;
import com.example.sulsul.common.type.EssayState;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.essay.dto.request.CreateEssayRequest;
import com.example.sulsul.essay.dto.request.RejectRequest;
import com.example.sulsul.essay.dto.response.*;
import com.example.sulsul.essay.entity.Essay;
import com.example.sulsul.essay.service.EssayService;
import com.example.sulsul.exception.essay.InvalidEssayCreateException;
import com.example.sulsul.exception.essay.InvalidRejectDetailException;
import com.example.sulsul.exception.essay.TeacherCreateEssayException;
import com.example.sulsul.exception.file.EmptyEssayFileException;
import com.example.sulsul.exceptionhandler.ErrorResponse;
import com.example.sulsul.fcm.FcmMessageService;
import com.example.sulsul.file.entity.File;
import com.example.sulsul.file.service.FileService;
import com.example.sulsul.notification.service.NotificationService;
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
    private final FcmMessageService fcmMessageService;
    private final NotificationService notificationService;

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
    public ResponseEntity<?> createEssay(@Parameter(description = "첨삭을 요청할 강사프로필의 id값")
                                         @PathVariable Long profileId,
                                         @ModelAttribute @Valid CreateEssayRequest request,
                                         @CurrentUser User user,
                                         BindingResult bindingResult) {
        // 첨삭 파일 여부 검증
        if (request.getEssayFile() == null || request.getEssayFile().isEmpty()) {
            throw new EmptyEssayFileException();
        }
        // 입력값 유효성 검사
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            throw new InvalidEssayCreateException(errorMap);
        }
        // 학생 유저만 첨삭요청 가능
        if (user.getUserType().equals(UType.TEACHER)) {
            throw new TeacherCreateEssayException(user.getId());
        }
        // 첨삭 엔티티 생성
        Essay essay = essayService.createEssay(profileId, user, request);
        // 첨삭 파일 업로드
        File file = fileService.uploadEssayFile(user, essay, request.getEssayFile());
        String filePath = file.getFilePath();

        // 첨삭요청 알림 전송
//        User target = essay.getTeacher();
//        String title = NotiTitle.REQUEST.getTitle();
//        String studentName = loginedUser.getName();
//        String body = NotiBody.REQUEST.getDetail(studentName);
//        notificationService.saveEssayNotification(title, body, target, essay);
//        fcmMessageService.sendToOne(target, title, body);

        // 첨삭요청 응답 생성
        RequestEssayResponse essayResponse = new RequestEssayResponse(essay, filePath);
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
                                                    @RequestParam("essayFile") MultipartFile essayFile,
                                                    @CurrentUser User user) {
        // 첨삭 파일 여부 검증
        if (essayFile == null || essayFile.isEmpty()) {
            throw new EmptyEssayFileException();
        }

        // 첨삭 엔티티 조회
        Essay essay = essayService.getEssayByIdAndEssyState(essayId, EssayState.PROCEED);
        // 강사 첨삭 파일 업로드
        fileService.uploadEssayFile(user, essay, essayFile);
        ProceedEssayResponse essayResponse = essayService.getProceedEssay(essayId);

        // 첨삭파일 업로드 알림 전송
//        User student = essay.getStudent();
//        User teacher = essay.getTeacher();
//        String teacherName = teacher.getName();
//        String title = NotiTitle.FILE.getTitle();
//        String body = NotiBody.FILE.getDetail(teacherName);
//        notificationService.saveEssayNotification(title, body, student, essay);
//        fcmMessageService.sendToOne(student, title, body);

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
    public ResponseEntity<?> getRequestEssays(@CurrentUser User user) {
        // 첨삭요청 목록 조회
        List<Essay> essays = essayService.getEssaysByUser(user, EssayState.REQUEST);
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
    public ResponseEntity<?> getProceedEssays(@CurrentUser User user) {
        // 진행중인 첨삭목록 조회
        List<Essay> essays = essayService.getEssaysByUser(user, EssayState.PROCEED);
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
    public ResponseEntity<?> getRejectEssays(@CurrentUser User user) {
        // 거절된 첨삭목록 조회
        List<Essay> essays = essayService.getEssaysByUser(user, EssayState.REJECT);
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
    public ResponseEntity<?> getCompleteEssays(@CurrentUser User user) {
        // 완료된 첨삭목록 조회
        List<Essay> essays = essayService.getEssaysByUser(user, EssayState.COMPLETE);
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
        RequestEssayResponse essayResponse = essayService.getEssayRequest(essayId);
        return new ResponseEntity<>(essayResponse, HttpStatus.OK);
    }

    @Operation(summary = "거절된 첨삭 개별조회", description = "거절된 첨삭을 개별조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RejectedEssayResponse.class))),
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
        RejectedEssayResponse essayResponse = essayService.getEssayReject(essayId);
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
        ProceedEssayResponse essayResponse = essayService.getProceedEssay(essayId);
        return new ResponseEntity<>(essayResponse, HttpStatus.OK);
    }

    @Operation(summary = "완료된 첨삭 개별조회", description = "완료된 첨삭을 개별조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {ReviewedEssayResponse.class, CompletedEssayResponse.class}))),
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
        Essay essay = essayService.getEssayByIdAndEssyState(essayId, EssayState.COMPLETE);
        // 리뷰가 작성된 경우
        if (essay.isReviewed()) {
            ReviewedEssayResponse essayResponse = essayService.getReviewedEssay(essayId);
            return new ResponseEntity<>(essayResponse, HttpStatus.OK);
        }
        // 리뷰가 아직 작성되지 않은 경우
        CompletedEssayResponse essayResponse = essayService.getCompleteEssay(essayId);
        return new ResponseEntity<>(essayResponse, HttpStatus.OK);
    }

    @Operation(summary = "첨삭요청 수락", description = "첨삭요청을 수락한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AcceptEssayResponse.class))),
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

        // 첨삭요청 수락 알림 전송
//        User student = essay.getStudent();
//        User teacher = essay.getTeacher();
//        String teacherName = teacher.getName();
//        String title = NotiTitle.ACCEPT.getTitle();
//        String body = NotiBody.ACCEPT.getDetail(teacherName);
//        notificationService.saveEssayNotification(title, body, student, essay);
//        fcmMessageService.sendToOne(student, title, body);

        return new ResponseEntity<>(new AcceptEssayResponse(essay), HttpStatus.OK);
    }

    @Operation(summary = "첨삭요청 거절", description = "첨삭요청을 거절한다.")
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
    @PutMapping(value = "/essay/{essayId}/reject", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> rejectEssay(@Parameter(description = "거절할 첨삭요청의 id")
                                         @PathVariable Long essayId,
                                         @Valid @RequestBody RejectRequest rejectRequest,
                                         BindingResult bindingResult) {
        // 거절사유 유효성 검사
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            throw new InvalidRejectDetailException(errorMap);
        }

        Essay essay = essayService.rejectEssay(essayId, rejectRequest);

        // 첨삭요청 거절 알림 전송
//        User student = essay.getStudent();
//        User teacher = essay.getTeacher();
//        String teacherName = teacher.getName();
//        String title = NotiTitle.REJECT.getTitle();
//        String body = NotiBody.REJECT.getDetail(teacherName);
//        notificationService.saveEssayNotification(title, body, student, essay);
//        fcmMessageService.sendToOne(student, title, body);

        return new ResponseEntity<>(new RejectEssayResponse(essay), HttpStatus.OK);
    }

    @Operation(summary = "진행중인 첨삭 완료", description = "진행중인 첨삭요청을 완료한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CompleteEssayResponse.class))),
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

        // 첨삭완료 알림 전송
//        User student = essay.getStudent();
//        User teacher = essay.getTeacher();
//        String teacherName = teacher.getName();
//        String title = NotiTitle.COMPLETE.getTitle();
//        String body = NotiBody.COMPLETE.getDetail(teacherName);
//        notificationService.saveEssayNotification(title, body, student, essay);
//        fcmMessageService.sendToOne(student, title, body);

        return new ResponseEntity<>(new CompleteEssayResponse(essay), HttpStatus.OK);
    }
}