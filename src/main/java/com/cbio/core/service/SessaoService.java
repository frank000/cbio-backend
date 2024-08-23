package com.cbio.core.service;

import com.cbio.app.entities.SessaoEntity;

import java.util.Optional;

public interface SessaoService {

    SessaoEntity validaOuCriaSessaoAtivaPorUsuarioCanal(Long usuarioId, String canal, Long agora);

    Boolean isSessaoValidaTempo(Long agora, SessaoEntity sessao);

    void atualizarSessao(SessaoEntity sessao, String ultimaAcao);

    Optional<SessaoEntity> buscaSessaoUsuarioCanal(Long identificadorUsuario, String canal);

    void salva(SessaoEntity sessao);

}
