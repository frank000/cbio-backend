package com.cbio.core.service;

import com.cbio.app.entities.SessaoEntity;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.EntradaMensagemDTO;
import io.minio.errors.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public interface ChatbotForwardService {

    void processaMensagem(EntradaMensagemDTO entradaMensagemDTO) throws Exception;
    DialogoDTO enviaRespostaDialogoPorCanal(CanalDTO canal, DialogoDTO dialogoResposta) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
    Optional<DialogoDTO> notifyUserClosingAttendance(String mensagem, String channelId, SessaoEntity sessaoEntity) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

}
