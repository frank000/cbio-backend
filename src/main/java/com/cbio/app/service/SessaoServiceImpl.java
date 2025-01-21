package com.cbio.app.service;

import com.cbio.app.base.interfaces.TriFunction;
import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.ContactEntity;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.repository.ContactRepository;
import com.cbio.app.repository.SessaoCustomRepository;
import com.cbio.app.repository.SessaoRepository;
import com.cbio.app.service.enuns.CanalSenderEnum;
import com.cbio.app.service.utils.PhoneNumberUtil;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.chat.dto.SessionFiltroDTO;
import com.cbio.chat.dto.WebsocketNotificationDTO;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.SessaoService;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.ContactDTO;
import com.cbio.core.v1.dto.UsuarioDTO;
import com.mongodb.client.result.UpdateResult;
import jakarta.ws.rs.NotFoundException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
@Slf4j
public class SessaoServiceImpl implements SessaoService {

    private final SessaoRepository sessaoRepository;
    private final SessaoCustomRepository sessaoCustomRepository;
    private final AuthService authService;
    private final MongoTemplate mongoTemplate;
    private final ContactRepository contactRepository;

    @Override
    public SessaoEntity buscaSessaoAtivaPorIdentificadorUsuario(Long usuarioId, String idCanal) {
        return sessaoRepository.findByAtivoAndIdentificadorUsuarioAndCanalIdCanal(Boolean.TRUE, usuarioId, idCanal)
                .orElseThrow(() -> new RuntimeException("Erro ao iniciar atendimento extorno - Sessão não encontrada"));
    }


    @Override
    public void salva(SessaoEntity sessao) {
        sessaoRepository.save(sessao);
    }

    @Override
    public SessaoEntity validaOuCriaSessaoAtivaPorUsuarioCanal(Long usuarioId, CanalDTO canal, Long agora) {

        Long expiresValue = 120000L;

        Optional<SessaoEntity> byAtivoAndIdentificadorUsuarioAndCanal = sessaoRepository.findByAtivoAndIdentificadorUsuarioAndCanalNomeAndCanalIdCanal(Boolean.TRUE, usuarioId, canal.getNome(), canal.getIdCanal());

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


    public Long closeAttendaceLastOneHour() {

        Update update = new Update();
        update.set("atendimentoAberto", Boolean.FALSE);
        update.set("dataHoraAtendimentoAberto", null);

        LocalDateTime oneHourPast = LocalDateTime.now().minusHours(1L);

        Query query = new Query().addCriteria(
                Criteria.where("atendimentoAberto").is(Boolean.TRUE)
                        .andOperator(Criteria.where("dataHoraAtendimentoAberto").lt(oneHourPast))
        );

        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, SessaoEntity.class);
        BsonValue upsertedId = updateResult.getUpsertedId();
        if (upsertedId != null) {
            upsertedId.asArray().forEach(bsonValue -> {
                log.info("Cada: {}", bsonValue);
            });

        }
        log.info("IDS para fechar", upsertedId);
        return updateResult.getModifiedCount();

    }

    public Long closeAttendaceWhatsappCloseWindow() {

        Update update = new Update();
        update.set("atendimentoAberto", Boolean.FALSE);
        update.set("dataHoraAtendimentoAberto", null);

        LocalDateTime oneHourPast = LocalDateTime.now().minusHours(23L);

        Query query = new Query().addCriteria(
                Criteria.where("canal.nome").is(CanalSenderEnum.WHATSAPP.name())
                        .andOperator(
                                Criteria.where("atendimentoAberto").is(Boolean.TRUE),
                                Criteria.where("dataHoraAtendimentoAberto").lt(oneHourPast))
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
                                    .nameCanal(sessaoEntity.getCanal().getNome().trim())
                                    .channelId(sessaoEntity.getLastChannelChat().getChannelUuid())
                                    .name(getNameOrNumber(sessaoEntity))
                                    .name(getNameOrNumber(sessaoEntity))
                                    .active(sessaoEntity.getAtendimentoAberto())
                                    .contact(sessaoEntity.getContact())
                                    .cpf(sessaoEntity.getCpf())
                                    .identificadorRemetente(String.valueOf(sessaoEntity.getIdentificadorUsuario()))
                                    .time(CbioDateUtils.getDateTimeFormated(sessaoEntity.getLastChannelChat().getDateTimeStart()))
                                    //.preview("ROCKETCHAT:Cliente solicita atendimento")
                                    .build()));

            return websocketNotificationDTOS;
        } else {

            throw new RuntimeException("Usuario não existe no token. Favor contactar os administradores.");
        }
    }

    @Nullable
    private String getNameOrNumber(SessaoEntity sessaoEntity) {
        return StringUtils.hasText(sessaoEntity.getNome()) ? sessaoEntity.getNome() : getNumber(sessaoEntity);
    }

    @Nullable
    private String getNumber(SessaoEntity sessaoEntity) {
        String result;

        if (CanalSenderEnum.WHATSAPP.name().equals(sessaoEntity.getCanal().getNome())) {
            result = PhoneNumberUtil.format(String.valueOf(sessaoEntity.getIdentificadorUsuario()), "+XX(XX)XXXX-XXXX", 12);
        } else {
            result = "Novo usuário";
        }
        return result;
    }

    @Override
    public SessaoEntity getSessionById(String id) {
        return sessaoRepository.findById(id).orElseThrow(() -> new RuntimeException("Sessão não encontrada."));
    }

    @Override
    public SessaoEntity getSessionByChannelId(String channelId) {
        return sessaoRepository.findByLastChannelChatChannelUuid(channelId)
                .orElseThrow(() -> new NotFoundException("Sessão não encontrada."));
    }

    @Override
    public void updateUserInfosIntoSession(String idSession, UsuarioDTO.UsuarioSessionFormDTO usuarioDTO) {
        SessaoEntity sessionById = getSessionById(idSession);
        sessionById.setCpf(usuarioDTO.getCpf());
        sessionById.setNome(usuarioDTO.getName());
        sessionById.setTelefone1(usuarioDTO.getTelefone1());
        sessionById.setTelefone2(usuarioDTO.getTelefone2());
        sessionById.setEmail(usuarioDTO.getEmail());
        sessaoRepository.save(sessionById);
    }

    @Override
    public void bindContactToSession(String idSession, ContactDTO dto) {
        SessaoEntity sessionById = getSessionById(idSession);
        sessionById.setContact(dto);
        sessionById.setNome(dto.getName());
        sessionById.setEmail(dto.getEmail());
        sessaoRepository.save(sessionById);

        ContactEntity contact = contactRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Contato não encontrado."));
        contact.getSessions().add(idSession);
        contactRepository.save(contact);
    }

    @Override
    public void disconnectAttendance(String channelId, TriFunction<String, String, SessaoEntity, Optional<DialogoDTO>> notify) {
        SessaoEntity sessaoEntity = getSessionByChannelId(channelId);
        sessaoEntity.setAtendimentoAberto(Boolean.FALSE);
        sessaoRepository.save(sessaoEntity);

        notify.apply(
                "Chat desconectado.",
                sessaoEntity.getLastChannelChat().getChannelUuid(),
                sessaoEntity
        );
    }


    @Override
    public void connectAttendance(String channelId) throws Exception {
        SessaoEntity sessionById = getSessionByChannelId(channelId);
        LocalDateTime now = LocalDateTime.now();
        sessionById.setAtendimentoAberto(Boolean.TRUE);
        verifyWindowToWhatsappChannel(sessionById, now);

        sessaoRepository.save(sessionById);
    }


    public void verifyWindowToWhatsappChannel(SessaoEntity sessaoEntity, LocalDateTime now) throws Exception {
        boolean isJanelaWhatsappClosed = sessaoEntity.getLastChannelChat() != null &&
                sessaoEntity.getLastChannelChat().getDateTimeStart().plusHours(23).isBefore(now);

        if (Boolean.TRUE.equals(sessaoEntity.getAtendimentoAberto()) &&
                CanalSenderEnum.WHATSAPP.name().equals(sessaoEntity.getCanal().getNome())
                && isJanelaWhatsappClosed) {
            throw new IllegalArgumentException("Janela fechada para envios de mensagens.");
        }
    }
}
