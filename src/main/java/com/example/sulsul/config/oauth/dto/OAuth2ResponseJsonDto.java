package com.example.sulsul.config.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2ResponseJsonDto {
    private String name;
    private String email;
    private String isGuest;
}