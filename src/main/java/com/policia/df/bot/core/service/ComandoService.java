package com.policia.df.bot.core.service;

import com.policia.df.bot.core.v1.dto.ComandoDTO;

public interface ComandoService {

    void adicionarComando(ComandoDTO comandoDTO);

    ComandoDTO buscarPorNome(String nome);

}
