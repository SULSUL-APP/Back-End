package com.example.sulsul.user.oauth2.userinfo;

import java.util.Map;

public class GoogleUserInfo extends Oauth2UserInfo {
    public GoogleUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
