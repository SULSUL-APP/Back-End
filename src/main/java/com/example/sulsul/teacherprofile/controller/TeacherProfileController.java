package com.example.sulsul.teacherprofile.controller;

import com.example.sulsul.common.CurrentUser;
import com.example.sulsul.common.type.EType;
import com.example.sulsul.exceptionhandler.ErrorResponse;
import com.example.sulsul.review.dto.response.ReviewGroupResponse;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.teacherprofile.dto.request.TeacherProfileRequest;
import com.example.sulsul.teacherprofile.dto.response.ProfileListResponse;
import com.example.sulsul.teacherprofile.dto.response.TeacherProfileResponse;
import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import com.example.sulsul.teacherprofile.service.TeacherProfileService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Profile", description = "강사 프로필 관련 API")
@RestController
@RequiredArgsConstructor
public class TeacherProfileController {

    private final TeacherProfileService teacherProfileService;

    @Operation(summary = "나의(강사) 프로필 조회", description = "나의 강사 프로필을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/profiles/myProfile")
    public ResponseEntity<?> getMyProfile(@CurrentUser User user) {

        TeacherProfile teacherProfile = teacherProfileService.getTeacherProfile(user);

        return new ResponseEntity<>(new TeacherProfileResponse(teacherProfile), HttpStatus.OK);
    }

    @Operation(summary = "강사 프로필 조회", description = "profileId에 해당하는 강사 프로필을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/profiles/{profileId}")
    public ResponseEntity<?> getTeacherProfile(@Parameter(description = "프로필을 조회할 강사의 id")
                                               @PathVariable Long profileId) {

        TeacherProfile teacherProfile = teacherProfileService.getTeacherProfile(profileId);

        return new ResponseEntity<>(new TeacherProfileResponse(teacherProfile), HttpStatus.OK);
    }

    @Operation(summary = "인문사회 강사 프로필 리스트 조회", description = "인문사회 강사 프로필 리스트를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileListResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/profiles/social")
    public ResponseEntity<?> getSocialProfileList() {

        List<TeacherProfile> profiles = teacherProfileService.getProfileList(EType.SOCIETY);
        return new ResponseEntity<>(new ProfileListResponse(profiles), HttpStatus.OK);
    }

    @Operation(summary = "수리과학 강사 프로필 리스트 조회", description = "수리과학 강사 프로필 리스트를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileListResponse.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/profiles/nature")
    public ResponseEntity<?> getNatureProfileList() {

        List<TeacherProfile> profiles = teacherProfileService.getProfileList(EType.NATURE);
        return new ResponseEntity<>(new ProfileListResponse(profiles), HttpStatus.OK);
    }

    @Operation(summary = "강사(자신)의 프로필 수정", description = "강사(자신)의 프로필을 수정한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CREATED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TeacherProfileRequest.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/profiles/{profileId}")
    public ResponseEntity<?> createTeacherProfile(@Parameter(description = "프로필을 수정할 강사 자신")
                                                  @CurrentUser User user,
                                                  @Parameter(description = "수정한 프로필 내용")
                                                  @RequestBody TeacherProfileRequest teacherProfileRequest) {

        teacherProfileService.createTeacherProfile(user, teacherProfileRequest);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
