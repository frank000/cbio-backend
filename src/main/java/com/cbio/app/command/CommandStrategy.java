package com.cbio.app.command;

import com.cbio.app.entities.SessaoEntity;
import com.cbio.core.v1.dto.DecisaoResposta;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public interface CommandStrategy {

    Map<String, BiFunction<String, SessaoEntity, List<DecisaoResposta>>> getFuncaoEtapas();

    default DecisaoResposta resolveDecisao(String texto, String proximaEtada) {
        return new DecisaoResposta(texto, proximaEtada);
    }
}
