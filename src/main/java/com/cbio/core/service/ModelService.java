package com.cbio.core.service;

import com.cbio.app.exception.CbioException;
import com.cbio.core.v1.dto.ModelDTO;
import com.cbio.core.v1.dto.SelecaoDTO;

import java.util.List;

public interface ModelService {

    ModelDTO save(ModelDTO dto) throws CbioException;

    ModelDTO update(ModelDTO dto) throws CbioException;

    List<ModelDTO> listAll();

    ModelDTO getById(String id) throws CbioException;

    void delete(String id) throws CbioException;

    List<SelecaoDTO> listSelection();

    ModelDTO getByName(String name) throws CbioException;
}
