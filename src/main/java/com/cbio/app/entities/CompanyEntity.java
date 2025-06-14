package com.cbio.app.entities;

import com.cbio.core.v1.enuns.EstadosEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("company")
@Getter
@Setter
@Builder
public class CompanyEntity {

    @Id
    private String id;

    private String nome;

    private String telefone;

    private String email;

    private String endereco;

    private String cidade;

    private LocalDateTime dataCadastro;

    @Enumerated(EnumType.STRING)
    private EstadosEnum estado;

    private String cep;

    private String tier;

    private Integer porta;

    @Getter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    private StatusPaymentEnum statusPayment;

    private LocalDateTime dataAlteracaoStatus;

    public StatusPaymentEnum getStatusPayment() {
        if (statusPayment == null) {
            statusPayment = StatusPaymentEnum.TRIAL;
        }
        return statusPayment;
    }
}
