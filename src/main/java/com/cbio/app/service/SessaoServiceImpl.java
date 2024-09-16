package com.cbio.app.service;

import com.cbio.app.base.utils.DateRocketUtils;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.repository.SessaoCustomRepository;
import com.cbio.app.repository.SessaoRepository;
import com.cbio.chat.dto.SessionFiltroDTO;
import com.cbio.chat.dto.WebsocketNotificationDTO;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.SessaoService;
import com.cbio.core.v1.dto.CanalDTO;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Data
public class SessaoServiceImpl implements SessaoService {

    private final SessaoRepository sessaoRepository;
    private final SessaoCustomRepository sessaoCustomRepository;
    private final AuthService authService;

    private final MongoTemplate mongoTemplate;

    @Override
    public SessaoEntity buscaSessaoAtivaPorIdentificadorUsuario(Long usuarioId) {
        return sessaoRepository.findByAtivoAndIdentificadorUsuario(Boolean.TRUE, usuarioId)
                .orElseThrow(() -> new RuntimeException("Erro ao iniciar atendimento extorno - Sessão não encontrada"));
    }

    @Override
    public Optional<SessaoEntity> buscaSessaoUsuarioCanal(Long identificadorUsuario, String canal) {
        return sessaoRepository.findByCanalAndIdentificadorUsuario(canal, identificadorUsuario);
    }

    @Override
    public void salva(SessaoEntity sessao) {
        sessaoRepository.save(sessao);
    }

    @Override
    public SessaoEntity validaOuCriaSessaoAtivaPorUsuarioCanal(Long usuarioId, CanalDTO canal, Long agora) {

        Long expiresValue = 120000L;

        Optional<SessaoEntity> byAtivoAndIdentificadorUsuarioAndCanal = sessaoRepository.findByAtivoAndIdentificadorUsuarioAndCanalNome(Boolean.TRUE, usuarioId, canal.getNome());

        return byAtivoAndIdentificadorUsuarioAndCanal
                .orElseGet(() -> criaESalvaSessao(usuarioId, agora, expiresValue, canal));

    }

    public SessaoEntity buscaSessaoAtivaPorUsuarioCanal(Long usuarioId, String canal, String channelId) {
        return sessaoRepository.findByAtivoAndIdentificadorUsuarioAndCanalNomeAndLastChannelChatChannelUuid(Boolean.TRUE, usuarioId, canal, channelId)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada."));

    }

    private @NotNull SessaoEntity expiraOuRenovaSessao(Long agora, SessaoEntity sessao, Long expiresValue) {
        if (agora > sessao.getExpiresAt()) {

            sessao.setAtivo(Boolean.FALSE);
            sessao.setFinalSessao(agora);

        } else { //renova
            sessao.setExpiresAt(agora + expiresValue); // 2 minutos
        }

        return sessaoRepository.save(sessao);
    }

    private @NotNull SessaoEntity criaESalvaSessao(Long usuarioId, Long agora, Long expiresValue, CanalDTO canal) {
        SessaoEntity sessao;
        sessao = SessaoEntity.builder()
                .sessaoId(UUID.randomUUID())
                .identificadorUsuario(usuarioId)
                .inicioSessao(agora)
                .ativo(Boolean.TRUE)
                .expiresAt(agora + expiresValue)
                .canal(canal)
                .build();

        return sessaoRepository.save(sessao);
    }

    @Override
    public Boolean isSessaoValidaTempo(Long agora, SessaoEntity sessao) {
        return agora <= sessao.getExpiresAt();
    }

    @Override
    public void atualizarSessao(SessaoEntity sessao, String ultimaAcao) {
        sessao.setUltimaEtapa(ultimaAcao);
        sessaoRepository.save(sessao);
    }

    String encode64(String string) {
        return Base64.getEncoder().encodeToString(string.getBytes());
    }

    String decode64(String b64) {
        byte[] decodedBytes = Base64.getDecoder().decode(b64);
        String decodedString = new String(decodedBytes);

        return decodedString;
    }


    public Long alteraTemplatesDeCertificado() {

        Update update = new Update();
        update.set("atendimentoAberto", Boolean.FALSE);
        update.set("dataHoraAtendimentoAberto", null);

        LocalDateTime oneHourPast = LocalDateTime.now().minusHours(1L);

        Query query = new Query().addCriteria(
                Criteria.where("atendimentoAberto").is(Boolean.TRUE)
                        .andOperator(Criteria.where("dataHoraAtendimentoAberto").lt(oneHourPast))
        );

        return mongoTemplate.updateMulti(query, update, SessaoEntity.class).getModifiedCount();

    }

    @Override
    public List<WebsocketNotificationDTO> getChatSessions() {

        Map<String, Object> claimsUserLogged = authService.getClaimsUserLogged();

        Object userId = claimsUserLogged.get("userId");


        if (userId != null) {

            List<WebsocketNotificationDTO> websocketNotificationDTOS = new ArrayList<>();
            SessionFiltroDTO filter = SessionFiltroDTO.builder()
                    .attendantId((String) userId)
                    .build();

            sessaoCustomRepository.buscaListaSessoes(filter)
                    .forEach(sessaoEntity ->
                            websocketNotificationDTOS.add(WebsocketNotificationDTO.builder()
                                    .userId(sessaoEntity.getId())
                                    .nameCanal(sessaoEntity.getCanal().getNome())
                                    .channelId(sessaoEntity.getLastChannelChat().getChannelUuid())
                                    .name(StringUtils.hasText(sessaoEntity.getNome()) ? sessaoEntity.getNome() : null)
                                    .active(true)
                                    .time(DateRocketUtils.getDateTimeFormated(sessaoEntity.getLastChannelChat().getDateTimeStart()))
                                    .preview("ROCKETCHAT:Cliente solicita atendimento")
                                    .build()));
            return websocketNotificationDTOS;
        } else {

            throw new RuntimeException("Usuario não existe no token. Favor contactar os administradores.");
        }
    }

    @Override
    public SessaoEntity getSessionById(String id) {
        return sessaoRepository.findById(id).orElseThrow(() -> new RuntimeException("Sessão não encontrada."));
    }
}
