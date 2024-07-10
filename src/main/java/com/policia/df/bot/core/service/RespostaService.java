package com.policia.df.bot.core.service;

import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;

import java.util.List;

public interface RespostaService {

    List<DecisaoResposta> decidirResposta(String texto, String ultimaAcao, SessaoEntity sessao) throws Exception;

}
