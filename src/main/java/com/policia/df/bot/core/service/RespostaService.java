package com.policia.df.bot.core.service;

import com.policia.df.bot.core.v1.dto.DecisaoResposta;
import okhttp3.RequestBody;

import java.io.IOException;

public interface RespostaService {

    DecisaoResposta decidirResposta(String texto, String ultimaAcao) throws Exception;

}
