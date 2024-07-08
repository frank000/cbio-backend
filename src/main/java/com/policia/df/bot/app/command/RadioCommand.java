package com.policia.df.bot.app.command;

import com.policia.df.bot.app.entities.EtapaEntity;
import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.app.repository.EtapaRepository;
import com.policia.df.bot.app.repository.SessaoRepository;
import com.policia.df.bot.core.service.KeycloakService;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Component("radioCommand")
@Getter
@Slf4j
public class RadioCommand implements CommandStrategy {

    private final SessaoRepository sessaoRepository;
    public Map<String, BiFunction<String, SessaoEntity, DecisaoResposta>> relacaoDeEtapasEFuncoes = new HashMap<>();

    private final KeycloakService keycloakService;

    private final EtapaRepository etapaRepository;

    public RadioCommand(KeycloakService keycloakService, SessaoRepository sessaoRepository, EtapaRepository etapaRepository) {

        this.keycloakService = keycloakService;
        this.sessaoRepository = sessaoRepository;
        this.etapaRepository = etapaRepository;

    }

    @PostConstruct
    private void init() {

        List<EtapaEntity> listaEtapas = etapaRepository.findAllByComandoEtapa("radioCommand");

        listaEtapas.forEach(e -> {
            if("texto".equalsIgnoreCase(e.getTipoEtapa())) {
                relacaoDeEtapasEFuncoes.put(e.getNomeEtapa(), (texto, sessao) -> resolveDecisao(e.getMensagemEtapa(), e.getProximaEtapa()));
            } else {
                relacaoDeEtapasEFuncoes.put(e.getNomeEtapa(), this::localizarRadio);
            }
        });
    }

    public DecisaoResposta localizarRadio(String texto, SessaoEntity sessao) {

        sessao.flush(sessaoRepository);
        return new DecisaoResposta("Foi", "");



    }

    @Override
    public Map<String, BiFunction<String, SessaoEntity, DecisaoResposta>> getFuncaoEtapas() {
        return relacaoDeEtapasEFuncoes;
    }
}
