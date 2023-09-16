package com.example.sulsul.config.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
public class OAuth2ResponseJsonDto {

    private String name;
    private String email;
    private String isGuest;
}
