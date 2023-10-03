package com.example.sulsul.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class KakaoAccount implements Serializable {

    public static final long serialVersionUID = 1234L;

    private String email;
    private KakaoProfile profile;
}