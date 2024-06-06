package com.policia.df.bot.core.service;

import com.policia.df.bot.app.entities.SessaoEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.UUID;

public interface SessaoService {

    SessaoEntity validateSession(Update update, Long agora);

    Boolean sessaoValida(Long agora, SessaoEntity sessao);

    void atualizarSessao(SessaoEntity sessao, String ultimaAcao);

}
