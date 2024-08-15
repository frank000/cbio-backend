package com.policia.df.bot.core.service;

import com.policia.df.bot.core.v1.dto.DialogoDTO;

import java.util.Optional;

public interface AssistentBotService {
    Optional<DialogoDTO> processaDialogoAssistent(DialogoDTO dialogo);
}
