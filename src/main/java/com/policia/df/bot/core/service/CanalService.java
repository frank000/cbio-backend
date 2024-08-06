package com.policia.df.bot.core.service;

import com.policia.df.bot.app.entities.CanalEntity;
import com.policia.df.bot.core.v1.dto.CanalDTO;
import com.policia.df.bot.core.v1.dto.EtapaDTO;

import java.util.List;

public interface CanalService {

    List<CanalEntity> listarTodos();

    CanalEntity incluirCanal(CanalEntity canal);

    CanalEntity findCanalByTokenAndCliente(String token, String cliente) throws Exception;

    void alterar(CanalDTO canal) throws Exception;

    Boolean existsByTokenAndCliente(String token, String cliente) throws Exception;

    void deleta(String id);

}
