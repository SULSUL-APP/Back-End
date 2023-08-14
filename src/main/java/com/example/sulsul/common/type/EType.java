package com.example.sulsul.common.type;

import com.example.sulsul.exception.user.ETypeNotFoundException;

import java.util.Arrays;

public enum EType {
    NATURE("NATURE"), SOCIETY("SOCIETY");

    private String value;

    EType(String eType) {
        this.value = eType;
    }

    public String getValue() {
        return value;
    }

    public static EType getEType(String eType) {
        return Arrays.stream(EType.values())
                .filter(etype -> etype.getValue().equals(eType))
                .findAny()
                .orElseThrow(ETypeNotFoundException::new);
    }

}

