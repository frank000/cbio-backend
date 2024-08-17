package com.policia.df.bot.core.service;

import com.policia.df.bot.app.entities.SessaoEntity;

public interface SessaoService {

    SessaoEntity validaOuCriaSessaoAtivaPorUsuario(Long usuarioId, Long agora);

    Boolean isSessaoValidaTempo(Long agora, SessaoEntity sessao);

    void atualizarSessao(SessaoEntity sessao, String ultimaAcao);

}
