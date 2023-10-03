package com.example.sulsul.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class KakaoAccount implements Serializable {

    private static final long serialVersionUID = -2638143251070509599L;

    private String email;
    private KakaoProfile profile;
}