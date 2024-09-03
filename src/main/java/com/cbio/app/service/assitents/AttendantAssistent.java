package com.cbio.app.service.assitents;

import com.cbio.app.base.utils.DateRocketUtils;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.chat.services.WebsocketPath;
import com.cbio.core.service.AssistentBotService;
import com.cbio.core.v1.dto.RasaMessageDTO;
import com.cbio.core.v1.dto.outchatmessages.AttendantMessageOutDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("attendantAssistent")
@RequiredArgsConstructor
public class AttendantAssistent implements AssistentBotService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public Optional<DialogoDTO> processaDialogoAssistent(DialogoDTO dialogo) {

        try {
            AttendantMessageOutDTO message = AttendantMessageOutDTO.builder()
                    .text(dialogo.getMensagem())
                    .toUserId(dialogo.getSessionId())
                    .fromUserId(dialogo.getToIdentifier())
                    .time(DateRocketUtils.getDateTimeFormated(dialogo.getCreatedDateTime()))
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(message);
            simpMessagingTemplate
                    .convertAndSend(String.format(WebsocketPath.Constants.CHAT, dialogo.getChannelUuid()),
                            json);

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
