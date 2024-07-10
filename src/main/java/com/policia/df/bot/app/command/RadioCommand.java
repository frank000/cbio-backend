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
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiFunction;

@Component("radioCommand")
@Getter
@Slf4j
public class RadioCommand implements CommandStrategy {

    @Value("${atendimento.url}")
    private String atendimentoUrl;

    private final SessaoRepository sessaoRepository;
    public Map<String, BiFunction<String, SessaoEntity, List<DecisaoResposta>>> relacaoDeEtapasEFuncoes = new HashMap<>();

    private final KeycloakService keycloakService;

    private final EtapaRepository etapaRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

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
                relacaoDeEtapasEFuncoes.put(e.getNomeEtapa(), (texto, sessao) -> List.of(resolveDecisao(e.getMensagemEtapa(), e.getProximaEtapa())));
            } else if("step_radio_identificacao".equalsIgnoreCase(e.getNomeEtapa())) {
                relacaoDeEtapasEFuncoes.put(e.getNomeEtapa(), this::buscarUsuarioPorMatriculaKeycloak);
            } else if("step_radio_confirmacao_usuario".equalsIgnoreCase(e.getNomeEtapa())) {
                relacaoDeEtapasEFuncoes.put(e.getNomeEtapa(), this::confirmaUsuario);
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

    public List<DecisaoResposta> localizarRadio(String texto, SessaoEntity sessao) throws IOException {

        EquipamentoRadioDTO equipamento = buscarEquipamento(atendimentoUrl, texto);

        if(equipamento == null) return List.of(new DecisaoResposta("Nenhum equipamento encontrado. Digite novamente.", "step_radio"));

        String dataHora = LocalDateTime.parse(
                equipamento.getUltimaPosicao().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .format(formatter)
                .toString();

        String enderecoEquipamento = buscarEnderecoEquipamento(atendimentoUrl, equipamento.getLongitude(), equipamento.getLatitude());

        DecisaoResposta resposta1 =
                new DecisaoResposta(
                        "nº do equipamento: " + equipamento.getId() + "\n" +
                              "Data/hora última localização: " + dataHora + "\n" +
                              "Endereço: " + enderecoEquipamento, "");

        DecisaoResposta resposta2 = new DecisaoResposta(createWazeString(equipamento.getLongitude(), equipamento.getLatitude()), "");

        DecisaoResposta resposta3 = new DecisaoResposta(createMapsString(equipamento.getLongitude(), equipamento.getLatitude()), "");

        sessao.flush(sessaoRepository);

        List<DecisaoResposta> lista = new ArrayList<>();

        lista.add(resposta1);
        lista.add(resposta2);
        lista.add(resposta3);

        return lista;

    }

    private String buscarEnderecoEquipamento(String url, String longitude, String latitude) throws IOException {

        String params = String.format("longitude=%s&latitude=%s", longitude, latitude);

        String endpoint = new StringBuilder(url)
                .append("/v1/endereco-app/reverse-geocoding?")
                .append(params)
                .toString();

        OkHttpClient client = new OkHttpClient();

        String token = "Bearer " + keycloakService.getToken();

        Request request = new Request.Builder()
                .url(endpoint)
                .addHeader("accept", "*/*")
                .addHeader("content-type", "text/plain")
                .addHeader("Authorization", token)
                .build();

        ResponseBody response = client.newCall(request).execute().body();

        ObjectMapper objectMapper = new ObjectMapper();

        String retorno = objectMapper.writeValueAsString(response.string());

        return retorno.replaceAll("\"", "");
    }


    private EquipamentoRadioDTO buscarEquipamento(String url, String texto) throws IOException {
        String endpoint = new StringBuilder(url)
                .append("/v1/arcgis/equipamento?numeroEquipamento=")
                .append(texto)
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

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.string(), EquipamentoRadioDTO.class);
        } catch (Exception e) {

            return null;
        }

    }

    private List<DecisaoResposta> buscarUsuarioPorMatriculaKeycloak(String texto, SessaoEntity sessao) {

        Optional<List<UserRepresentation>> listUsuarios = keycloakService.pesquisarUsuarioPorMatricula(texto.toLowerCase().trim());

        if(listUsuarios.isPresent()) {
            return List.of(
                    new DecisaoResposta(listUsuarios.get().get(0).getFirstName(), "step_radio_confirmacao_usuario"),
                    new DecisaoResposta("Confirma os dados do solicitante? ","step_radio_confirmacao_usuario")
            );
        }

        return List.of(new DecisaoResposta("Usuário não encontrado. Digite novamente.", "step_radio_identificacao"));
    }

    private List<DecisaoResposta> confirmaUsuario(String texto, SessaoEntity sessao) {
        if("sim".equalsIgnoreCase(texto)) {
            return List.of(new DecisaoResposta("Digite o número do equipamento.", "step_radio"));
        } else {
            return List.of(new DecisaoResposta("Digite novamente a matrícula.", "step_radio_identificacao"));
        }
    }

    private String  createWazeString(String longitude, String latitude) {
        return new StringBuilder("https://www.waze.com/ul?ll=")
                    .append(latitude)
                    .append("%2C")
                    .append(longitude)
                    .append("&navigate=yes&zoom=15")
                    .toString();
    }

    private String createMapsString(String longitude, String latitude) {
        return new StringBuilder("https://www.google.com/maps/search/?api=1&query=")
                .append(latitude)
                .append("%2C")
                .append(longitude)
                .toString();
    }

    @Override
    public Map<String, BiFunction<String, SessaoEntity, List<DecisaoResposta>>> getFuncaoEtapas() {
        return relacaoDeEtapasEFuncoes;
    }
}
