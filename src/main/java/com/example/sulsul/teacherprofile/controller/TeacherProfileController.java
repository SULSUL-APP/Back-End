package com.example.sulsul.teacherprofile.controller;

import com.example.sulsul.exceptionhandler.ErrorResponse;
import com.example.sulsul.review.dto.response.ReviewGroupResponse;
import com.example.sulsul.review.dto.response.ReviewResponse;
import com.example.sulsul.review.entity.Review;
import com.example.sulsul.teacherprofile.dto.response.TeacherProfileResponse;
import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import com.example.sulsul.teacherprofile.service.TeacherProfileService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Profile", description = "강사 프로필 관련 API")
@RestController
@RequiredArgsConstructor
public class TeacherProfileController {

    private final TeacherProfileService teacherProfileService;

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

        return new ResponseEntity<>(new TeacherProfileResponse(teacherProfile.getTeacher(), teacherProfile), HttpStatus.OK);
    }
}
