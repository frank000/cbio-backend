package com.policia.df.bot.core.service;

import com.policia.df.bot.app.entities.CanalEntity;
import com.policia.df.bot.core.v1.dto.CanalDTO;
import com.policia.df.bot.core.v1.dto.EtapaDTO;

import java.util.List;

public interface CanalService {

    public List<CanalEntity> listarTodos();

    public CanalEntity incluirCanal(CanalEntity canal);

    public CanalEntity findCanalByTokenAndCliente(String token, String cliente) throws Exception;

    public void alterar(CanalDTO canal) throws Exception;

}
