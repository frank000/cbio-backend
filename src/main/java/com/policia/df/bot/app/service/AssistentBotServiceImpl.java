package com.policia.df.bot.app.service;

import ch.qos.logback.core.net.ObjectWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.policia.df.bot.app.client.RasaClient;
import com.policia.df.bot.core.service.AssistentBotService;
import com.policia.df.bot.core.v1.dto.DialogoDTO;
import com.policia.df.bot.core.v1.dto.RasaMessageDTO;
import com.policia.df.bot.core.v1.dto.RasaMessageInDTO;
import com.policia.df.bot.core.v1.dto.RasaMessageOutDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssistentBotServiceImpl implements AssistentBotService {

    private final RasaClient rasaClient;

    public Optional<DialogoDTO> processaDialogoAssistent(DialogoDTO dialogo) {

        try {
            RasaMessageOutDTO rasaMessageDTO = RasaMessageOutDTO
                    .builder()
                    .mensagem(dialogo.getMensagem())
                    .identificadorRemetente(dialogo.getIdentificadorRemetente())
                    .build();

            List<RasaMessageDTO> webhook = rasaClient.webhook(rasaMessageDTO);

            List<DialogoDTO> respostas = webhook
                    .stream()
                    .map(o ->
                            {
                                return DialogoDTO.builder()
                                        .mensagem(o.getText())
                                        .buttons(getButtons(o))
                                        .identificadorRemetente(o.getIdentificadorId())
                                        .build();
                            }
                    )
                    .collect(Collectors.toList());

            if(respostas.isEmpty()){
                log.info("ASSISTENT-BOT-RASA: Não obtivemos respostas");
            }
            return respostas.stream().findFirst();

        } catch (Exception e) {
            String msg = String.format("Envio para o Assistente Rasa com problema: %s", e.getMessage());
            throw new RuntimeException(msg);
        }

    }

    @NotNull
    private static List<RasaMessageDTO.Button> getButtons(RasaMessageDTO o) {
        return o.getButtons() != null && !o.getButtons().isEmpty() ? o.getButtons() : Collections.emptyList();
    }
}
