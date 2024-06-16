package com.policia.df.bot.app.command;

import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.app.repository.SessaoRepository;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Component("resetCommand")
@RequiredArgsConstructor
public class ResetCommand implements CommandStrategy{

    public Map<String, BiFunction<String, SessaoEntity, DecisaoResposta>> relacaoDeEtapasEFuncoes = new HashMap<>();

    private final SessaoRepository sessaoRepository;

    @Override
    public Map<String, BiFunction<String, SessaoEntity, DecisaoResposta>> getFun() {
        relacaoDeEtapasEFuncoes.put("init", (texto, sessao) -> getServiçoReiniciadoELimpaSessao(sessao));

        return relacaoDeEtapasEFuncoes;
    }

    private DecisaoResposta getServiçoReiniciadoELimpaSessao(SessaoEntity sessao) {
        sessao.flush(sessaoRepository);
        return resolveDecisao("Serviço reiniciado", "");
    }


}
