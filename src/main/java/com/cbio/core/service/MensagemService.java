package com.cbio.core.service;

import org.telegram.telegrambots.meta.api.objects.Update;


public interface MensagemService {

    void salvarMensagem(Update update, Long identificadorCanal, String sessao);

}
