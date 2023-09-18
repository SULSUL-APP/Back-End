package com.example.sulsul.refreshtoken;

import com.example.sulsul.config.jwt.JwtTokenProvider;
import com.example.sulsul.config.jwt.dto.JwtTokenDto;
import com.example.sulsul.exception.jwt.NotExpiredTokenException;
import com.example.sulsul.exception.jwt.TokenNotFoundException;
import com.example.sulsul.exception.refresh.InvalidRefreshTokenException;
import com.example.sulsul.exception.refresh.RefreshTokenNotFoundException;
import com.example.sulsul.exceptionhandler.ErrorResponse;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "RefreshToken", description = "RefreshToken 관련 api")
@RestController
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "AccessToken 재발급", description = "RefreshToken의 유효기간이 3일 이내인 경우 RefreshToken도 재발급한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "UNAUTHORIZED",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "FORBIDDEN",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {

        // access token 추출
        String accessToken = jwtTokenProvider.resolveToken(request);
        if (accessToken == null) {
            throw new TokenNotFoundException();
        }

        // expired access token인지 확인
        Claims claims = jwtTokenProvider.getExpiredTokenClaims(accessToken);
        if (claims == null) {
            throw new NotExpiredTokenException();
        }

        // refresh token 추출 후 유효성 검사
        String refreshToken = request.getHeader("RefreshToken");
        if (refreshToken == null) {
            throw new RefreshTokenNotFoundException();
        }
        if (!jwtTokenProvider.validate(refreshToken)) {
            throw new InvalidRefreshTokenException();
        }

        // 토큰 재발급
        JwtTokenDto tokensDto = refreshTokenService.refresh(refreshToken, claims);
        HttpHeaders headers = new HttpHeaders();
        headers.set("AccessToken", "Bearer " + tokensDto.getAccessToken());

        String newRefreshToken = tokensDto.getRefreshToken();
        if (newRefreshToken != null) {
            headers.set("RefreshToken", newRefreshToken);
        }

        return new ResponseEntity<>("토큰 재발급 완료", headers, HttpStatus.OK);
    }
}