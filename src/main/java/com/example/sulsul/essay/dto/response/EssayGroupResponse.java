package com.example.sulsul.essay.dto.response;

import com.example.sulsul.essay.entity.Essay;
import lombok.Getter;

import java.util.List;

@Getter
public class EssayGroupResponse {

    private List<EssayResponse> essays;

    public EssayGroupResponse(List<Essay> essays) {
        essays.stream().map(EssayResponse::new)
                .forEach(essay -> this.essays.add(essay));
    }
}