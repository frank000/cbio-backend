package com.cbio.rasa.action.telegram;

import com.cbio.app.base.utils.DateRocketUtils;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.chat.dto.ChatChannelInitializationDTO;
import com.cbio.chat.dto.WebsocketNotificationDTO;
import com.cbio.chat.services.ChatService;
import com.cbio.chat.services.WebsocketPath;
import com.cbio.core.service.AttendantService;
import com.cbio.core.service.SessaoService;
import com.cbio.core.service.UserService;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.UsuarioDTO;
import io.github.jrasa.Action;
import io.github.jrasa.CollectingDispatcher;
import io.github.jrasa.domain.Domain;
import io.github.jrasa.event.Event;
import io.github.jrasa.exception.RejectExecuteException;
import io.github.jrasa.message.Message;
import io.github.jrasa.tracker.Tracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitAttendantTelegramAction implements Action {

    private final SessaoService sessaoService;
    private final AttendantService attendantService;
    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserService userService;

    @Override
    public String name() {
        return "ac_talk_attendant_telegram";
    }

//    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public List<? extends Event> run(CollectingDispatcher dispatcher, Tracker tracker, Domain domain) throws RejectExecuteException {
        log.info("ACTION: ac_init_attendant");
        String text = "Você está sendo transferido para um atendente.";

        if (tracker.getCurrentState() != null && tracker.getCurrentState().getSenderId() != null) {

            String senderId = tracker.getCurrentState().getSenderId();

            SessaoEntity sessaoEntity = sessaoService
                    .buscaSessaoAtivaPorIdentificadorUsuario(Long.valueOf(senderId));

            try {
                LocalDateTime now = LocalDateTime.now();

                UsuarioDTO attendantDTO = attendantService.fetch();
                ChatChannelInitializationDTO chatChannelInitialization = ChatChannelInitializationDTO.builder()
                        .userIdOne(attendantDTO.getId())
                        .userIdTwo(sessaoEntity.getId())
                        .initCanal(sessaoEntity.getCanal().getNome())
                        .build();

                String channelUuid = chatService.establishChatSession(chatChannelInitialization, now);

                sessaoEntity.setLastChannelChat(
                        SessaoEntity.ChannelChatDTO.builder()
                                .channelUuid(channelUuid)
                                .dateTimeStart(now)
                                .build());
                sessaoEntity.setAtendimentoAberto(Boolean.TRUE);
                sessaoEntity.setUltimoAtendente(attendantDTO);
                sessaoEntity.setDataHoraAtendimentoAberto(now);
                sessaoService.salva(sessaoEntity);


                simpMessagingTemplate
                        .convertAndSend(
                                String.format(WebsocketPath.Constants.CHAT, attendantDTO.getId()),
                                WebsocketNotificationDTO.builder()
                                        .userId(sessaoEntity.getId())
                                        .channelId(channelUuid)
                                        .name(StringUtils.hasText(sessaoEntity.getNome())? sessaoEntity.getNome() : null)
                                        .active(true)
                                        .time(DateRocketUtils.getDateTimeFormated(now))
                                        .preview("ROCKETCHAT:Cliente solicita atendimento")
                                        .build());
                //TODO disparar websocket para lista do UserID

                //TODO disparar websocket para lista do atendente

            } catch (Exception e) {
                text = e.getMessage();
            }


            dispatcher
                    .utterMessage(Message
                            .builder()
                            .text(text)
                            .build());
        }

        return Action.empty();
    }
}