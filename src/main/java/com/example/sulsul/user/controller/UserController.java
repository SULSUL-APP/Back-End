package com.example.sulsul.user.controller;

import com.amazonaws.Response;
import com.example.sulsul.common.CurrentUser;
import com.example.sulsul.exceptionhandler.ErrorResponse;
import com.example.sulsul.review.dto.response.ReviewResponse;
import com.example.sulsul.review.service.ReviewService;
import com.example.sulsul.user.dto.UserResponse;
import com.example.sulsul.user.dto.request.SignUpDto;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
@Tag(name = "Users", description = "유저 관련 API")
public class UserController {

    private final UserService userService;
    @Operation(summary = "Guest 유저에 대한 추가 회원가입", description = "Guest 유저에 대해 추가 정보를 작성한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/sign-up")
    public ResponseEntity<Void> signUp(@CurrentUser User user,
                                       @RequestBody(required = false) SignUpDto signUpDto) {

        log.info("[SignUpDto]: {}", signUpDto.getUType());
        log.info("[SignUpDto]: {}", signUpDto.getEType());
        log.info("[현재 로그인한 유저]: {}", user.getEmail());
        userService.signUp(user, signUpDto);

        return ResponseEntity.noContent().build();
    }

}
