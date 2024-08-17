package com.policia.df.bot.app.entities;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "atendente")
public class AtendenteEntity {

    @Id
    private String id;

    private String nome;

    private Boolean ativo;

}
