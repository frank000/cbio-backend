package com.cbio.app.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comando")
@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ComandoEntity {

    @Id
    private String id;

    private String nome;

    private String nomeServico;

    private String descricao;

    private Boolean ativo;

}
