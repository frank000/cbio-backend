package com.cbio.app.service.assitents;

import com.cbio.app.client.RasaClient;
import com.cbio.core.service.AssistentBotService;
import com.cbio.core.v1.dto.DialogoDTO;
import com.cbio.core.v1.dto.RasaMessageDTO;
import com.cbio.core.v1.dto.RasaMessageOutDTO;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service("attendantAssistent")
@RequiredArgsConstructor
public class AttendantAssistent implements AssistentBotService {

    private final RasaClient rasaClient;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public Optional<DialogoDTO> processaDialogoAssistent(DialogoDTO dialogo) {

        try {
            RasaMessageOutDTO rasaMessageDTO = RasaMessageOutDTO
                    .builder()
                    .mensagem(dialogo.getMensagem())
                    .identificadorRemetente(dialogo.getIdentificadorRemetente())
                    .build();


            simpMessagingTemplate
                    .convertAndSend("/topic/demo", rasaMessageDTO);
            return Optional.empty();

        } catch (Exception e) {
            String msg = String.format("Envio para o Assistente Attendant com problema: %s", e.getMessage());
            throw new RuntimeException(msg);
        }

    }

    @NotNull
    private static List<RasaMessageDTO.Button> getButtons(RasaMessageDTO o) {
        return o.getButtons() != null && !o.getButtons().isEmpty() ? o.getButtons() : Collections.emptyList();
    }
}
