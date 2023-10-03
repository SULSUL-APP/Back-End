package com.example.sulsul.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class KakaoUserInfo implements Serializable {

    public static final long serialVersionUID = 4321L;

    private KakaoAccount kakao_account;
}