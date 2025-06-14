package com.cbio.core.v1.dto;

import com.cbio.app.entities.StatusPaymentEnum;
import com.cbio.core.v1.enuns.EstadosEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CompanyDTO implements Serializable {

    private String id;

    private String nome;

    private String telefone;

    private String email;

    private String endereco;

    private String cidade;

    private EstadosEnum estado;

    private String cep;

    private String tier;

    private Integer porta;

    private StatusPaymentEnum statusPayment;


    private LocalDateTime dataAlteracaoStatus;
}
