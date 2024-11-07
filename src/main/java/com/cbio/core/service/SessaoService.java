package com.cbio.core.service;

import com.cbio.app.base.interfaces.TriFunction;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.chat.dto.WebsocketNotificationDTO;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.ContactDTO;
import com.cbio.core.v1.dto.UsuarioDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessaoService {

    SessaoEntity validaOuCriaSessaoAtivaPorUsuarioCanal(Long usuarioId, CanalDTO canal, Long agora);


    SessaoEntity buscaSessaoAtivaPorIdentificadorUsuario(Long usuarioId, String idCanal);

    Boolean isSessaoValidaTempo(Long agora, SessaoEntity sessao);

    void atualizarSessao(SessaoEntity sessao, String ultimaAcao);


    void salva(SessaoEntity sessao);

    Long closeAttendaceLastOneHour();

    Long closeAttendaceWhatsappCloseWindow();

    List<WebsocketNotificationDTO> getChatSessions();

    SessaoEntity getSessionById(String id);

    SessaoEntity getSessionByChannelId(String channelId);

    void updateUserInfosIntoSession(String idSession, UsuarioDTO.UsuarioSessionFormDTO usuarioDTO);

    void bindContactToSession(String idSession, ContactDTO dto);

    void disconnectAttendance(String channelId, TriFunction<String, String, SessaoEntity, Optional<DialogoDTO>> notify);

    void connectAttendance(String channelId) throws Exception;

    void verifyWindowToWhatsappChannel(SessaoEntity sessaoEntity, LocalDateTime now) throws Exception;
}
