package com.policia.df.bot.app.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.policia.df.bot.app.configuration.keycloak.KeycloakConfig;
import com.policia.df.bot.app.entities.EtapaEntity;
import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.app.repository.EtapaRepository;
import com.policia.df.bot.app.repository.SessaoRepository;
import com.policia.df.bot.core.service.KeycloakService;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;
import com.policia.df.bot.core.v1.dto.EquipamentoRadioDTO;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
                relacaoDeEtapasEFuncoes.put(e.getNomeEtapa(), (texto, sessao) -> {
                    try {
                        return localizarRadio(texto, sessao);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        });
    }

    public DecisaoResposta localizarRadio(String texto, SessaoEntity sessao) throws IOException {

        String endpoint = new StringBuilder("https://hml-atendimento.api.policia.df.gov.br/v1/arcgis/equipamento?numeroEquipamento=" + texto)
                .toString();

        OkHttpClient client = new OkHttpClient();

        String token = "Bearer " + keycloakService.getToken();

        Request request = new Request.Builder()
                .url(endpoint)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("Authorization", token)
                .build();

        ResponseBody response = client.newCall(request).execute().body();

        ObjectMapper objectMapper = new ObjectMapper();

        EquipamentoRadioDTO equipamento = objectMapper.readValue(response.string(), EquipamentoRadioDTO.class);

        sessao.flush(sessaoRepository);

        return new DecisaoResposta(createMapString(equipamento.getLongitude(), equipamento.getLatitude()), "");

    }

    private String  createMapString(String longitude, String latitude) {
        return "https://www.waze.com/ul?ll=" + latitude + "%2C" + longitude + "&navigate=yes&zoom=17";
    }

    @Override
    public Map<String, BiFunction<String, SessaoEntity, DecisaoResposta>> getFuncaoEtapas() {
        return relacaoDeEtapasEFuncoes;
    }
}
