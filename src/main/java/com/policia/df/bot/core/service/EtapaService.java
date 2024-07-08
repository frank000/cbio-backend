package com.policia.df.bot.core.service;

import com.policia.df.bot.core.v1.dto.EtapaDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EtapaService {

    public void adicionarEtapa(EtapaDTO etapa);

    public List<EtapaDTO> listar();

    public void alterar(EtapaDTO etapaDTO) throws Exception;

}
