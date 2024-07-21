package com.policia.df.bot.core.v1.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PessoaDTO {

    private String policialMatricula;

    private String quadroSigla;

    private String postoSigla;

    private String nome;

}
