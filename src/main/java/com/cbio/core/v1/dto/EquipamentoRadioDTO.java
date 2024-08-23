package com.cbio.core.v1.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class EquipamentoRadioDTO {

    private String id;

    private String ultimaPosicao;

    private String placa;

    private String batalhao;

    private String nomeVeiculo;

    private String complemento;

    private String longitude;

    private String latitude;

    private String endereco;

}