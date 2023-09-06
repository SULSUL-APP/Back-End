package com.example.sulsul.user.service;

import com.example.sulsul.common.type.EType;
import com.example.sulsul.common.type.UType;
import com.example.sulsul.exception.user.UserNotFoundException;
import com.example.sulsul.user.dto.request.SignUpDto;
import com.example.sulsul.user.entity.User;
import com.example.sulsul.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    /**
     * 초기 데이터는 Name, Email, ProfileImage.
     * 유저의 타입과 분야에 대한 추가정보를 받아서 추가 회원가입을 진행한다.
     * @param user 추가정보를 입력할 user 정보.
     * @param signUpDto 추가 회원가입 정보 :  [ 유저 타입, 논술 분야 ].
     */
    public void signUp(User user, SignUpDto signUpDto) {

        user.updateEType(EType.valueOf(signUpDto.getEType()));
        user.updateUType(UType.valueOf(signUpDto.getUType()));

        userRepository.save(user);
    }
}
