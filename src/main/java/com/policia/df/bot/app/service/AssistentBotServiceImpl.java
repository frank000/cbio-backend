package com.policia.df.bot.app.service;

import com.policia.df.bot.app.client.RasaClient;
import com.policia.df.bot.core.service.AssistentBotService;
import com.policia.df.bot.core.v1.dto.DialogoDTO;
import com.policia.df.bot.core.v1.dto.RasaMessageInDTO;
import com.policia.df.bot.core.v1.dto.RasaMessageOutDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

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

            List<Map<String, String>>  webhook = rasaClient.webhook(rasaMessageDTO);
            List<DialogoDTO> respostas = webhook
                    .stream()
                    .map(o ->
                            {
                                Map<String, String> y = (Map<String, String>) o;
                                return DialogoDTO.builder()
                                        .mensagem(y.get("text"))
                                        .identificadorRemetente(y.get("recipient_id"))
                                        .build();
                            }
                    )
                    .collect(Collectors.toList());

            return respostas.stream().findFirst();

        } catch (Exception e) {
            String msg = String.format("Envio para o Assistente Rasa com problema: %s", e.getMessage());
            throw new RuntimeException(msg);
        }

    }
}
