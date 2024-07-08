package com.policia.df.bot.core.service;

import com.policia.df.bot.core.v1.dto.ComandoDTO;
import com.policia.df.bot.core.v1.dto.EtapaDTO;

import java.util.List;

public interface ComandoService {

    void adicionarComando(ComandoDTO comandoDTO);

    ComandoDTO buscarPorNome(String nome);

    List<ComandoDTO> listarComandos();

    public void alterar(ComandoDTO comandoDTO) throws Exception;

}
