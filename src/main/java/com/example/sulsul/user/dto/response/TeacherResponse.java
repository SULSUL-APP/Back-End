package com.example.sulsul.user.dto.response;

import com.example.sulsul.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class TeacherResponse extends LoginResponse {

    @Schema(description = "강사 catchphrase", example = "catchphrase 예시")
    private final String catchPhrase;

    public TeacherResponse(User user) {
        super(user);
        this.catchPhrase = user.getCatchPhrase();
    }
}
