package com.example.sulsul.user.service;

import com.example.sulsul.common.type.DType;
import com.example.sulsul.common.type.EType;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.teacherprofile.entity.TeacherProfile;
import com.example.sulsul.teacherprofile.repository.TeacherProfileRepository;
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
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    /**
     * 초기 데이터는 Name, Email, ProfileImage.
     * 유저의 타입과 분야에 대한 추가정보를 받아서 추가 회원가입을 진행한다.
     * @param user 추가정보를 입력할 user 정보.
     * @param signUpRequest 추가 회원가입 정보 :  [ 유저 타입, 논술 분야 ].
     */
    public void signUp(User user, SignUpRequest signUpRequest) {

        user.updateEType(EType.valueOf(signUpRequest.getEssayType()));
        user.updateUType(UType.valueOf(signUpRequest.getUserType()));
        user.updateUserRole(Role.USER);

        if(Objects.equals(signUpRequest.getUserType(), UType.TEACHER.getValue())){
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
    public StudentResponse putStudentMyPage(User user, PutMyPageRequest putMyPageRequest) {

        user.updateEType(EType.getEType(putMyPageRequest.getEssayType()));
        user.updateEmail(putMyPageRequest.getEmail());

        userRepository.save(user);

        return new StudentResponse(user);
    }

    /**
     * @param user 마이페이지를 수정할 선생 정보.
     */
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
    public CommonResponse deleteUser(User user) {

        user.updateDType(DType.DELETE);
        userRepository.save(user);

        return new CommonResponse();
    }
}
