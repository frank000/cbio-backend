package com.cbio.core.service;

import com.cbio.core.v1.dto.DialogoDTO;

import java.util.Optional;

public interface AssistentBotService {
    Optional<DialogoDTO> processaDialogoAssistent(DialogoDTO dialogo);
}
