package com.cbio.core.service;

import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.EntradaMensagemDTO;

public interface ChatbotForwardService {

    void processaMensagem(EntradaMensagemDTO entradaMensagemDTO) throws Exception;
    void enviaRespostaDialogoPorCanal(CanalDTO canal, DialogoDTO dialogoResposta);
}
