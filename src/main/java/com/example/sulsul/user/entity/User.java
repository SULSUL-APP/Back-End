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
@Getter
@Builder
@Setter(AccessLevel.PROTECTED)
@Table(name = "users")
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

    @Column()
    private String catchPhrase;

    @Enumerated(EnumType.STRING)
    private UType uType;

    @Enumerated(EnumType.STRING)
    private EType eType;

    @Enumerated(EnumType.STRING)
    private DType dType = DType.AVAILABLE;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;


    public void updateName(String name) {
        this.name = name;
    }

    public void updateCatchPhrase(String catchPhrase) {
        this.catchPhrase = catchPhrase;
    }

    public void updateUType(UType uType) {
        this.uType = uType;
    }

    public void updateEType(EType eType) {
        this.eType = eType;
    }

    public void updateDType(DType dType) {
        this.dType = dType;
    }

}