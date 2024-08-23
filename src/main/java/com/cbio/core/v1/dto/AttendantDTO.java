package com.cbio.core.v1.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttendantDTO {

    private String id;

    private String nome;

    private Boolean ativo;

}
