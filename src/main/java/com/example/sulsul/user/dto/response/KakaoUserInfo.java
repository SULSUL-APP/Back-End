package com.example.sulsul.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class KakaoUserInfo implements Serializable {

    private static final long serialVersionUID = -6530926418824951362L;

    private KakaoAccount kakao_account;
}