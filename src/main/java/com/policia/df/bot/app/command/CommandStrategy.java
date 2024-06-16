package com.policia.df.bot.app.command;

import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface CommandStrategy {

    Map<String, BiFunction<String, SessaoEntity, DecisaoResposta>> getFun();

    default DecisaoResposta resolveDecisao(String texto, String acao) {
        return new DecisaoResposta(texto, acao);
    }
}
