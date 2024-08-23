package com.cbio.app.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "etapa")
@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class EtapaEntity {

    @Id
    private String id;

    private String comandoEtapa;

    private String nomeEtapa;

    private String descricaoEtapa;

    private String mensagemEtapa;

    private String proximaEtapa;

    private String tipoEtapa;

    private Boolean ativo;

}
