package com.cbio.core.service;

import com.cbio.app.entities.SessaoEntity;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.EntradaMensagemDTO;

import java.util.Optional;

public interface ChatbotForwardService {

    void processaMensagem(EntradaMensagemDTO entradaMensagemDTO) throws Exception;
    DialogoDTO enviaRespostaDialogoPorCanal(CanalDTO canal, DialogoDTO dialogoResposta);
    Optional<DialogoDTO> notifyUserClosingAttendance(String mensagem, String channelId, SessaoEntity sessaoEntity);

}
