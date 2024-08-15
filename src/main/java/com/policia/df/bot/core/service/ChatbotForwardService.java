package com.policia.df.bot.core.service;

import com.policia.df.bot.core.v1.dto.EntradaMensagemDTO;

public interface ChatbotForwardService {

    void processaMensagem(EntradaMensagemDTO entradaMensagemDTO);
}
