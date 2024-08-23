package com.cbio.core.v1.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ComandoDTO {

    private String id;

    private String nome;

    private String nomeServico;

    private String descricao;

    private Boolean ativo;

}
