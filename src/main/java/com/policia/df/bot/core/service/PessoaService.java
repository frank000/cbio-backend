package com.policia.df.bot.core.service;

import com.policia.df.bot.core.v1.dto.PessoaDTO;

import java.io.IOException;

public interface PessoaService {

    String buscarPorMatricula(String matricula) throws IOException;

}
