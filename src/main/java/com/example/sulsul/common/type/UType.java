package com.example.sulsul.common.type;

import com.example.sulsul.exception.notFound.UTypeNotFoundException;

import java.util.Arrays;

public enum UType {
    TEACHER("TEACHER"),
    STUDENT("STUDENT");

    private String value;

    UType(String uType) {
        this.value = uType;
    }

    public String getValue() {
        return value;
    }

    public static UType getUType(String uType) {
        return Arrays.stream(UType.values())
                .filter(utype -> utype.getValue().equals(uType))
                .findAny()
                .orElseThrow(UTypeNotFoundException::new);
    }
}
