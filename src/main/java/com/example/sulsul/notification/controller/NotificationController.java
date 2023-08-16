package com.example.sulsul.notification.controller;

import com.example.sulsul.exception.notification.InvalidCommonNotiRequestException;
import com.example.sulsul.exceptionhandler.ErrorResponse;
import com.example.sulsul.fcm.FcmMessageService;
import com.example.sulsul.notification.dto.CommonNotiRequest;
import com.example.sulsul.notification.dto.CommonNotiResponse;
import com.example.sulsul.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Notification", description = "알림 관련 API")
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final FcmMessageService fcmMessageService;

    @Operation(summary = "전체알림 전송", description = "전체알림을 전송한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommonNotiResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/noti/common")
    public ResponseEntity<?> sendCommonNoti(@RequestBody @Valid CommonNotiRequest request,
                                            BindingResult bindingResult) {

        // 전체알림 요청 유효성 검사
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            throw new InvalidCommonNotiRequestException(errorMap);
        }

        String title = request.getTitle();
        String body = request.getBody();

        // 전체알림 전송
        notificationService.saveCommonNotification(title, body);
        fcmMessageService.sendToAll(title, body);
        return new ResponseEntity<>(new CommonNotiResponse(), HttpStatus.OK);
    }
}