package com.cbio.core.service;

import com.cbio.app.entities.CanalEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.core.v1.dto.CanalDTO;

import java.util.List;
import java.util.Optional;

public interface CanalService {

    List<CanalEntity> listarTodos();

    CanalDTO obtemPorId(String id);

    void delete(String id);

    CanalDTO incluirCanal(CanalDTO canal);

    Optional<CanalEntity> findCanalByTokenAndCliente(String token, String cliente) throws Exception;

    void alterar(CanalDTO canal) throws Exception;

    Boolean existsByTokenAndCliente(String token, String cliente) throws Exception;

    void deleta(String id);

    CanalDTO getCanalByCompanyIdAndNome(String companyId, String nome) throws CbioException;
}
