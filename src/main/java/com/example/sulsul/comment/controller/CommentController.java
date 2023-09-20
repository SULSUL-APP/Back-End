package com.example.sulsul.comment.controller;

import com.example.sulsul.comment.dto.request.CommentRequest;
import com.example.sulsul.comment.dto.response.CommentGroupResponse;
import com.example.sulsul.comment.dto.response.CommentResponse;
import com.example.sulsul.comment.dto.response.DeleteSuccessResponse;
import com.example.sulsul.comment.entity.Comment;
import com.example.sulsul.comment.service.CommentService;
import com.example.sulsul.common.CurrentUser;
import com.example.sulsul.essay.service.EssayService;
import com.example.sulsul.exception.comment.InvalidCommentCreateException;
import com.example.sulsul.exception.comment.InvalidCommentUpdateException;
import com.example.sulsul.exceptionhandler.ErrorResponse;
import com.example.sulsul.fcm.FcmMessageService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Comment", description = "댓글 관련 API")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final EssayService essayService;
    private final CommentService commentService;
    private final NotificationService notificationService;
    private final FcmMessageService fcmMessageService;

    @Operation(summary = "해당 첨삭의 모든 댓글 조회", description = "essayId에 해당하는 첨삭에 작성된 모든 댓글을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentGroupResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/essay/{essayId}/comments")
    public ResponseEntity<?> getComments(@Parameter(description = "댓글을 조회할 첨삭의 id")
                                         @PathVariable Long essayId) {
        List<Comment> comments = commentService.getComments(essayId);
        return new ResponseEntity<>(new CommentGroupResponse(comments), HttpStatus.OK);
    }

    @Operation(summary = "해당 첨삭에 댓글 작성", description = "essayId에 해당하는 첨삭에 댓글을 작성한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/essay/{essayId}/comments")
    public ResponseEntity<?> createComment(@Parameter(description = "댓글을 작성할 첨삭의 id")
                                               @PathVariable Long essayId,
                                           @Valid @RequestBody CommentRequest commentRequest,
                                           @CurrentUser User user,
                                           BindingResult bindingResult) {
        // 댓글 내용 유효성 검사
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            throw new InvalidCommentCreateException(errorMap);
        }

        // 댓글 생성
        Comment comment = commentService.createComment(essayId, user, commentRequest);

        // 댓글작성 알림전송
//        Essay essay = essayService.getEssayById(essayId);
//        String name = loginedUser.getName();
//        String title = NotiTitle.COMMENT.getTitle();
//        String body = NotiBody.COMMENT.getDetail(name);

//        if (loginedUser.isTeacher()) { // 강사가 댓글을 작성한 경우
//            User student = essay.getStudent();
//            notificationService.saveEssayNotification(title, body, student, essay);
//            fcmMessageService.sendToOne(student, title, body);
//        } else { // 학생이 댓글을 작성한 경우
//            User teacher = essay.getTeacher();
//            notificationService.saveEssayNotification(title, body, teacher, essay);
//            fcmMessageService.sendToOne(teacher, title, body);
//        }

        return new ResponseEntity<>(new CommentResponse(comment), HttpStatus.CREATED);
    }

    @Operation(summary = "댓글 수정", description = "commentId에 해당하는 댓글을 수정한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<?> updateComment(@Parameter(description = "수정할 댓글의 id")
                                           @PathVariable Long commentId,
                                           @Valid @RequestBody CommentRequest commentRequest,
                                           BindingResult bindingResult) {
        // 댓글 내용 유효성 검사
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            throw new InvalidCommentUpdateException(errorMap);
        }

        Comment comment = commentService.updateComment(commentId, commentRequest);
        return new ResponseEntity<>(new CommentResponse(comment), HttpStatus.OK);
    }

    @Operation(summary = "댓글 삭제", description = "commentId에 해당하는 댓글을 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeleteSuccessResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@Parameter(description = "삭제할 댓글의 id")
                                           @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>(new DeleteSuccessResponse(), HttpStatus.OK);
    }
}