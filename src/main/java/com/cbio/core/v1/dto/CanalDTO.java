package com.cbio.core.v1.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class CanalDTO implements Serializable {

    private String id;

    private String nome;

    private String token;

    private String idCanal;

    private String primeiroNome;

    private String userName;

    private String apiKey;

    private String cliente;

    private Boolean ativo;

    private CompanyDTO company;

}
