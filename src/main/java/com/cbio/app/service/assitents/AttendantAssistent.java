package com.cbio.app.service.assitents;

import com.cbio.app.base.utils.CbioDateUtils;
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
            AttendantMessageOutDTO.AttendantMessageOutDTOBuilder builder = AttendantMessageOutDTO.builder();
            builder
                    .text(dialogo.getMensagem())
                    .id(dialogo.getId())
                    .toUserId(dialogo.getSessionId())
                    .channelId(dialogo.getChannelUuid())
                    .fromUserId(dialogo.getToIdentifier())
                    .time(CbioDateUtils.getDateTimeFormated(dialogo.getCreatedDateTime()))
                    .type(dialogo.getType())
                    .media(dialogo.getMedia());

            if(dialogo.getMedia() != null){
                builder.type(dialogo.getMedia().getMediaType());
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(builder.build());
            enviarWebsocketParaChannelId(dialogo, json);
            enviarWebsocketParaNoficacao(dialogo, json);
            return Optional.empty();

        } catch (Exception e) {
            String msg = String.format("Envio para o Assistente Attendant com problema: %s", e.getMessage());
            throw new RuntimeException(msg);
        }

    }

    private void enviarWebsocketParaChannelId(DialogoDTO dialogo, String json) {
        simpMessagingTemplate
                .convertAndSend(String.format(WebsocketPath.Constants.CHAT, dialogo.getChannelUuid()),
                        json);
        log.info("Enviado para websokcet - " + String.format(WebsocketPath.Constants.CHAT, dialogo.getChannelUuid()) + " - " + json);
    }

    private void enviarWebsocketParaNoficacao(DialogoDTO dialogo, String json) {
        simpMessagingTemplate
                .convertAndSend(String.format(WebsocketPath.Constants.NOTIFICATION, dialogo.getToIdentifier()),
                        json);
        log.info("Notificação websokcet - " + String.format(WebsocketPath.Constants.NOTIFICATION, dialogo.getToIdentifier()) + " - " + json);
    }

    @NotNull
    private static List<RasaMessageDTO.Button> getButtons(RasaMessageDTO o) {
        return o.getButtons() != null && !o.getButtons().isEmpty() ? o.getButtons() : Collections.emptyList();
    }



}
