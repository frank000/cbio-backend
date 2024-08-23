package com.cbio.core.service;

import com.cbio.core.v1.dto.EntradaMensagemDTO;

public interface ChatbotForwardService {

    void processaMensagem(EntradaMensagemDTO entradaMensagemDTO);
}
