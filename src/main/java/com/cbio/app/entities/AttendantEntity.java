package com.cbio.app.entities;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "atendente")
public class AttendantEntity {

    @Id
    private String id;

    private String nome;

    private Boolean ativo;

}
