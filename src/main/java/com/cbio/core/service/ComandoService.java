package com.cbio.core.service;

import com.cbio.core.v1.dto.ComandoDTO;

import java.util.List;

public interface ComandoService {

    void adicionarComando(ComandoDTO comandoDTO);

    ComandoDTO buscarPorNome(String nome);

    List<ComandoDTO> listarComandos();

    public void alterar(ComandoDTO comandoDTO) throws Exception;

}
