package com.cbio.app.service.assitents;

import com.cbio.app.client.RasaClient;
import com.cbio.core.service.AssistentBotService;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.v1.dto.RasaMessageDTO;
import com.cbio.core.v1.dto.outchatmessages.RasaMessageOutDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service("rasaAssistent")
@RequiredArgsConstructor
public class RasaAssistent implements AssistentBotService {

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
