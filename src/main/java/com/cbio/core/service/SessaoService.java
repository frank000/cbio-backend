package com.cbio.core.service;

import com.cbio.app.entities.SessaoEntity;
import com.cbio.core.v1.dto.CanalDTO;

import java.util.Optional;

public interface SessaoService {

    SessaoEntity validaOuCriaSessaoAtivaPorUsuarioCanal(Long usuarioId, CanalDTO canal, Long agora);

    SessaoEntity buscaSessaoAtivaPorUsuarioCanal(Long usuarioId, String canal, String channelId);

    Boolean isSessaoValidaTempo(Long agora, SessaoEntity sessao);

    void atualizarSessao(SessaoEntity sessao, String ultimaAcao);

    Optional<SessaoEntity> buscaSessaoUsuarioCanal(Long identificadorUsuario, String canal);

    void salva(SessaoEntity sessao);

    Long alteraTemplatesDeCertificado();
}
