package com.cbio.app.entities;

import com.cbio.core.v1.dto.CompanyDTO;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document("canal")
public class CanalEntity {

    @Id
    private String id;

    private String nome;

    private String token;

    @Indexed
    private String idCanal;

    private String primeiroNome;

    private String userName;

    private String apiKey;

    private String cliente;

    private Boolean ativo;

    private CompanyDTO company;

}
