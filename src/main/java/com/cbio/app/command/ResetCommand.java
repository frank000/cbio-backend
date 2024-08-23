package com.cbio.app.command;

import com.cbio.app.entities.EtapaEntity;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.repository.EtapaRepository;
import com.cbio.app.repository.SessaoRepository;
import com.cbio.core.v1.dto.DecisaoResposta;
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

    public Map<String, BiFunction<String, SessaoEntity, List<DecisaoResposta>>> relacaoDeEtapasEFuncoes = new HashMap<>();

    private final SessaoRepository sessaoRepository;

    private final EtapaRepository etapaRepository;

    @PostConstruct
    private void init() {

        List<EtapaEntity> listaEtapas = etapaRepository.findAllByComandoEtapa("resetCommand");

        listaEtapas.forEach(e -> {
            if("texto".equalsIgnoreCase(e.getTipoEtapa())) {

                relacaoDeEtapasEFuncoes.put(e.getNomeEtapa(), (texto, sessao) -> List.of(resolveDecisao(e.getMensagemEtapa(), e.getProximaEtapa())));

            } else {

                relacaoDeEtapasEFuncoes.put(e.getNomeEtapa(), this::getServiçoReiniciadoELimpaSessao);

            }
        });
    }



    @Override
    public Map<String, BiFunction<String, SessaoEntity, List<DecisaoResposta>>> getFuncaoEtapas() {
        return relacaoDeEtapasEFuncoes;
    }

    private List<DecisaoResposta> getServiçoReiniciadoELimpaSessao(String texto, SessaoEntity sessao) {
        sessao.flush(sessaoRepository);
        return List.of(resolveDecisao("Serviço reiniciado", ""));
    }


}
