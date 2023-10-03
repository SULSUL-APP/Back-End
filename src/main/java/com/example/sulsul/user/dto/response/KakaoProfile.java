package com.example.sulsul.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class KakaoProfile implements Serializable {

    private static final long serialVersionUID = 601815126155343260L;

    private String nickname;
    private String profile_image_url;
}