package com.cbio.core.service;

import com.cbio.app.entities.SessaoEntity;
import com.cbio.core.v1.dto.DecisaoResposta;

import java.util.List;

public interface RespostaService {

    List<DecisaoResposta> decidirResposta(String texto, String ultimaAcao, SessaoEntity sessao) throws Exception;

}
