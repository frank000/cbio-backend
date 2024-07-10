package com.policia.df.bot.core.v1.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

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