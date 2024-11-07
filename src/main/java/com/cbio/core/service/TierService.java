package com.cbio.core.service;

import com.cbio.core.v1.dto.ComandoDTO;
import com.cbio.core.v1.dto.TierDTO;

import java.util.List;

public interface TierService {
    TierDTO salva(TierDTO tierDTO);

    List<TierDTO> listAll();
}
