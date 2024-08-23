package com.cbio.core.v1.dto;

import lombok.Data;

@Data
public class EtapaDTO {

    private String id;

    private String comandoEtapa;

    private String nomeEtapa;

    private String descricaoEtapa;

    private String mensagemEtapa;

    private String proximaEtapa;

    private String tipoEtapa;

    private Boolean ativo;

}
