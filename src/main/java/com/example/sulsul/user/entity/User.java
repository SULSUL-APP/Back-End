package com.example.sulsul.user.entity;

import com.example.sulsul.common.BaseEntity;
import com.example.sulsul.common.type.DType;
import com.example.sulsul.common.type.EType;
import com.example.sulsul.common.type.LoginType;
import com.example.sulsul.common.type.UType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "유저 이름은 필수 값입니다.")
    private String name;

    @Column(nullable = false)
    private String email;

    private String profileImage; // 프로필 이미지 경로

    private String catchPhrase;

    @Enumerated(EnumType.STRING)
    private UType userType;

    @Enumerated(EnumType.STRING)
    private EType essayType;

    @Enumerated(EnumType.STRING)
    private DType userState;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role userRole;

    public void updateUserRole(Role userRole) {
        this.userRole = userRole;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateCatchPhrase(String catchPhrase) {
        this.catchPhrase = catchPhrase;
    }

    public void updateUType(UType uType) {
        this.userType = uType;
    }

    public void updateEType(EType eType) {
        this.essayType = eType;
    }

    public void updateDType(DType dType) {
        this.userState = dType;
    }

    public boolean isTeacher() {
        return userType.equals(UType.TEACHER);
    }
}