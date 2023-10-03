package com.example.sulsul.user.service;

import com.example.sulsul.common.type.DType;
import com.example.sulsul.common.type.EType;
import com.example.sulsul.common.type.LoginType;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.config.jwt.JwtTokenProvider;
import com.example.sulsul.config.jwt.dto.JwtTokenDto;
import com.example.sulsul.exception.user.UserNotFoundException;
import com.example.sulsul.fcm.entity.FcmToken;
import com.example.sulsul.fcm.repository.FcmTokenRepository;
import com.example.sulsul.refreshtoken.RefreshToken;
import com.example.sulsul.refreshtoken.RefreshTokenRepository;
import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import com.example.sulsul.teacherprofile.repository.TeacherProfileRepository;
import com.example.sulsul.user.dto.response.KakaoUserInfo;
import com.example.sulsul.user.dto.request.PutMyPageRequest;
import com.example.sulsul.user.dto.request.SignUpRequest;
import com.example.sulsul.user.dto.response.CommonResponse;
import com.example.sulsul.user.dto.response.StudentResponse;
import com.example.sulsul.user.dto.response.TeacherResponse;
import com.example.sulsul.user.entity.Role;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final JwtTokenProvider tokenProvider;
    /**
     * 초기 데이터는 Name, Email, ProfileImage.
     * 유저의 타입과 분야에 대한 추가정보를 받아서 추가 회원가입을 진행한다.
     *
     * @param user          추가정보를 입력할 user 정보.
     * @param signUpRequest 추가 회원가입 정보 :  [ 유저 타입, 논술 분야 ].
     */
    @Transactional
    public void signUp(User user, SignUpRequest signUpRequest) {

        user.updateEType(EType.valueOf(signUpRequest.getEssayType()));
        user.updateUType(UType.valueOf(signUpRequest.getUserType()));
        user.updateUserRole(Role.USER);

        if (Objects.equals(signUpRequest.getUserType(), UType.TEACHER.getValue())) {
            TeacherProfile teacherProfile = new TeacherProfile(user, "0.0", 0);
            teacherProfileRepository.save(teacherProfile);
        }

        userRepository.save(user);
    }

    /**
     * @param user 마이페이지를 조회할 학생 정보.
     */
    public StudentResponse getStudentMyPage(User user) {

        return new StudentResponse(user);
    }

    /**
     * @param user 마이페이지를 조회할 선생 정보.
     */
    public TeacherResponse getTeacherMyPage(User user) {

        return new TeacherResponse(user);
    }

    /**
     * @param user 마이페이지를 수정할 학생 정보.
     */
    @Transactional
    public StudentResponse putStudentMyPage(User user, PutMyPageRequest putMyPageRequest) {

        user.updateEType(EType.getEType(putMyPageRequest.getEssayType()));
        user.updateEmail(putMyPageRequest.getEmail());

        userRepository.save(user);

        return new StudentResponse(user);
    }

    /**
     * @param user 마이페이지를 수정할 선생 정보.
     */
    @Transactional
    public TeacherResponse putTeacherMyPage(User user, PutMyPageRequest putMyPageRequest) {

        user.updateEType(EType.getEType(putMyPageRequest.getEssayType()));
        user.updateCatchPhrase(putMyPageRequest.getCatchPhrase());
        user.updateEmail(putMyPageRequest.getEmail());

        userRepository.save(user);

        return new TeacherResponse(user);
    }

    /**
     * @param user 탈퇴시킬 회원.
     */
    @Transactional
    public CommonResponse deleteUser(User user) {

        // RefreshToken 삭제
        refreshTokenRepository.findByUserId(user.getId())
                .ifPresent(refreshToken -> refreshTokenRepository.delete(refreshToken));

        user.delete();
        userRepository.save(user);

        return new CommonResponse();
    }

    /**
     * 카카오 리소스 서버에 사용자 정보 요청
     * @param accessToken 카카오 액세스 토큰
     * @return ResponseEntity<KakaoUserInfo> 반환
     */
    public ResponseEntity<KakaoUserInfo> postWithAccessToken(String accessToken) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8");
        headers.add("Authorization", "Bearer " + accessToken);

        RequestEntity<?> request = RequestEntity
                .post(uri)
                .headers(headers)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(request, KakaoUserInfo.class);
    }

    private void manageRegisteredUser(User user) {
        // 기존 유저의 refreshToken이 존재하는 경우 제거
        refreshTokenRepository.findByUserId(user.getId())
                .ifPresent(refreshTokenRepository::delete);

        // 기존 유저의 fcmToken이 존재하는 경우 제거
        fcmTokenRepository.findByUser(user)
                .ifPresent(fcmTokenRepository::delete);
    }
    @Transactional
    public User saveOrUpdate(KakaoUserInfo attributes, String fcmToken){
        // 카카오 유저정보 조회
        String email = attributes.getKakao_account()
                .getEmail();

        String name = attributes.getKakao_account()
                .getProfile()
                .getNickname();

        String profileImageUrl = attributes.getKakao_account()
                .getProfile()
                .getProfile_image_url();

        User user;
        // 기존 유저가 존재하는 경우
        if (userRepository.existsByEmail(email)) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(UserNotFoundException::new);

            user.update(name, profileImageUrl);
            manageRegisteredUser(user);
            log.info("[카카오 유저 등록 확인] user_id: {}", user.getId());
            log.info("[카카오 유저 등록 확인] user_profile_url: {}", user.getProfileImage());

        } else { // 새로 회원가입 하는 경우
            user = User.builder()
                    .name(name)
                    .email(email)
                    .profileImage(profileImageUrl)
                    .userRole(Role.GUEST)
                    .loginType(LoginType.KAKAO)
                    .userState(DType.AVAILABLE)
                    .build();

            log.info("[카카오 유저 등록] user_id: {}", user.getId());
        }

        // 유저 생성 or 업데이트
        User savedUser = userRepository.save(user);
        // Fcm Token 저장
        fcmTokenRepository.save(new FcmToken(user, fcmToken));

        return savedUser;
    }

    @Transactional
    public JwtTokenDto getToken(User user){

        JwtTokenDto jwtTokenDto = tokenProvider.createJwtToken(user.getEmail());
        refreshTokenRepository.save(new RefreshToken(user.getId(), jwtTokenDto.getRefreshToken()));
        return jwtTokenDto;
    }

    // refresh 토큰 저장
    // FCM 토큰 저장
    // access, refresh 토큰 생성해서 반환, 게스트 여부도 전달
}