package com.policia.df.bot.core.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


public interface MensagemService {

    void salvarMensagem(Update update, Long canal, String sessao);

}
