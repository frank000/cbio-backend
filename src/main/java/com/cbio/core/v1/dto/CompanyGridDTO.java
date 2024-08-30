package com.cbio.core.v1.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class CompanyGridDTO  implements Serializable {

    private String id;

    private String nome;

    private String cidade;

}
