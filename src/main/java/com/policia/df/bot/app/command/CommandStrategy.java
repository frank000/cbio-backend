package com.policia.df.bot.app.command;

import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public interface CommandStrategy {

    Map<String, BiFunction<String, SessaoEntity, List<DecisaoResposta>>> getFuncaoEtapas();

    default DecisaoResposta resolveDecisao(String texto, String proximaEtada) {
        return new DecisaoResposta(texto, proximaEtada);
    }
}
