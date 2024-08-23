package com.cbio.core.service;

import com.cbio.app.entities.CanalEntity;
import com.cbio.core.v1.dto.CanalDTO;

import java.util.List;

public interface CanalService {

    List<CanalEntity> listarTodos();

    CanalEntity incluirCanal(CanalEntity canal);

    CanalEntity findCanalByTokenAndCliente(String token, String cliente) throws Exception;

    void alterar(CanalDTO canal) throws Exception;

    Boolean existsByTokenAndCliente(String token, String cliente) throws Exception;

    void deleta(String id);

}
