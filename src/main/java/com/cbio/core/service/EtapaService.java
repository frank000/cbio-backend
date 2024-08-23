package com.cbio.core.service;

import com.cbio.core.v1.dto.EtapaDTO;

import java.util.List;

public interface EtapaService {

    public void adicionarEtapa(EtapaDTO etapa);

    public List<EtapaDTO> listar();

    public void alterar(EtapaDTO etapaDTO) throws Exception;

}
