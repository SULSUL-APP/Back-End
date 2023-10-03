package com.example.sulsul.user.controller;

import com.example.sulsul.common.CurrentUser;
import com.example.sulsul.config.jwt.dto.JwtTokenDto;
import com.example.sulsul.handler.ErrorResponse;
import com.example.sulsul.user.dto.response.KakaoUserInfo;
import com.example.sulsul.user.dto.request.KakaoTokenRequest;
import com.example.sulsul.user.dto.request.PutMyPageRequest;
import com.example.sulsul.user.dto.request.SignUpRequest;
import com.example.sulsul.user.dto.response.LoginResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
@Tag(name = "Users", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "token을 통한 소셜 로그인", description = "token 값을 받아서 유저 정보를 반환 받아 회원가입을 진행한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CREATE",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/auth/token/kakao")
    public ResponseEntity<?> socialLogin(@RequestBody KakaoTokenRequest kakaoTokenRequest) {

        log.info("[현재 요청된 token 값]: {}", kakaoTokenRequest.getKakaoToken());
        ResponseEntity<KakaoUserInfo> response = userService.postWithAccessToken(kakaoTokenRequest.getKakaoToken());
        KakaoUserInfo userInfo = response.getBody();

//        System.out.println(userInfo.getKakao_account().getEmail());
//        System.out.println(userInfo.getKakao_account().getProfile().getNickname());
//        System.out.println(userInfo.getKakao_account().getProfile().getProfile_image_url());

        User loginedUser = userService.saveOrUpdate(userInfo, kakaoTokenRequest.getFcmToken());

        JwtTokenDto jwtTokenDto = userService.getToken(loginedUser);

        // refresh 토큰 저장


        // access, refresh 토큰 생성해서 반환, 게스트 여부도 전달
        return new ResponseEntity<>(jwtTokenDto, HttpStatus.OK);
    }

    @Operation(summary = "Guest 유저에 대한 추가 회원가입", description = "Guest 유저에 대해 추가 정보를 작성한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/sign-up")
    public ResponseEntity<?> signUp(@CurrentUser User user,
                                    @RequestBody SignUpRequest signUpRequest) {

        log.info("[현재 로그인한 유저]: {}", user.getEmail());
        userService.signUp(user, signUpRequest);

        return new ResponseEntity<>(new LoginResponse(user), HttpStatus.OK);
    }

    @Operation(summary = "마이페이지 조희", description = "유저의 마이페이지를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/mypage")
    public ResponseEntity<?> getMyPage(@CurrentUser User user) {

        log.info("[현재 로그인한 유저]: {}", user.getEmail());

        if (user.isTeacher())
            return new ResponseEntity<>(userService.getTeacherMyPage(user), HttpStatus.OK);
        else
            return new ResponseEntity<>(userService.getStudentMyPage(user), HttpStatus.OK);
    }

    @Operation(summary = "마이페이지 수정", description = "유저의 마이페이지를 수정한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/mypage")
    public ResponseEntity<?> putMyPage(@CurrentUser User user,
                                       @RequestBody PutMyPageRequest putMyPageRequest) {

        log.info("[현재 로그인한 유저]: {}", user.getEmail());

        if (user.isTeacher())
            return new ResponseEntity<>(userService.putTeacherMyPage(user, putMyPageRequest), HttpStatus.OK);
        else
            return new ResponseEntity<>(userService.putStudentMyPage(user, putMyPageRequest), HttpStatus.OK);
    }

    @Operation(summary = "회원 탈퇴", description = "유저를 탈퇴시킨다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(value = "/delete")
    public ResponseEntity<?> deleteUser(@CurrentUser User user) {

        log.info("[현재 로그인한 유저]: {}", user.getEmail());

        return new ResponseEntity<>(userService.deleteUser(user), HttpStatus.OK);
    }
}
