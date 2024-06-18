package com.policia.df.bot.core.v1.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class EtapaDTO {

    private String id;

    private String comandoEtapa;

    private String descricaoEtapa;

    private Integer ordemEtapa;

    private String mensagemEtapa;

    private String proximaEtapa;

    private String tipoEtapa;

}
