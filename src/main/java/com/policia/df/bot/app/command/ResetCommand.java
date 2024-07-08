package com.policia.df.bot.app.command;

import com.policia.df.bot.app.entities.EtapaEntity;
import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.app.repository.EtapaRepository;
import com.policia.df.bot.app.repository.SessaoRepository;
import com.policia.df.bot.app.service.enuns.EtapaPadraoEnum;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Component("resetCommand")
@RequiredArgsConstructor
public class ResetCommand implements CommandStrategy{

    public Map<String, BiFunction<String, SessaoEntity, DecisaoResposta>> relacaoDeEtapasEFuncoes = new HashMap<>();

    private final SessaoRepository sessaoRepository;

    private final EtapaRepository etapaRepository;

    @PostConstruct
    private void init() {

        List<EtapaEntity> listaEtapas = etapaRepository.findAllByComandoEtapa("resetCommand");

        listaEtapas.forEach(e -> {
            if("texto".equalsIgnoreCase(e.getTipoEtapa())) {

                relacaoDeEtapasEFuncoes.put(e.getNomeEtapa(), (texto, sessao) -> resolveDecisao(e.getMensagemEtapa(), e.getProximaEtapa()));

            } else {

                relacaoDeEtapasEFuncoes.put(e.getNomeEtapa(), this::getServiçoReiniciadoELimpaSessao);

            }
        });

//        relacaoDeEtapasEFuncoes.put(EtapaPadraoEnum.INIT.getValor(), (texto, sessao) -> resolveDecisao("Por favor, digite o nome de usuário. Ex: fulano.ciclano.", "step_otp"));
//        relacaoDeEtapasEFuncoes.put("step_otp", this::keycloak);
    }



    @Override
    public Map<String, BiFunction<String, SessaoEntity, DecisaoResposta>> getFuncaoEtapas() {
//        relacaoDeEtapasEFuncoes.put(EtapaPadraoEnum.INIT.getValor(), (texto, sessao) -> getServiçoReiniciadoELimpaSessao(sessao));

        return relacaoDeEtapasEFuncoes;
    }

    private DecisaoResposta getServiçoReiniciadoELimpaSessao(String texto, SessaoEntity sessao) {
        sessao.flush(sessaoRepository);
        return resolveDecisao("Serviço reiniciado", "");
    }


}
