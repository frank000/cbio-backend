package com.cbio.core.v1.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class PhraseDTO implements Serializable {
    private String id;

    private String description;

    private Boolean ativo;

    private CompanyDTO company;
}
