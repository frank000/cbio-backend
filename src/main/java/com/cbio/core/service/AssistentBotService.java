package com.cbio.core.service;

import com.cbio.chat.dto.DialogoDTO;

import java.util.Optional;

public interface AssistentBotService {
    Optional<DialogoDTO> processaDialogoAssistent(DialogoDTO dialogo);
}
