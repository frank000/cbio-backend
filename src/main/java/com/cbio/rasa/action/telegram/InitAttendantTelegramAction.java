package com.cbio.rasa.action.telegram;

import com.cbio.app.entities.SessaoEntity;
import com.cbio.chat.dto.ChatChannelInitializationDTO;
import com.cbio.chat.services.ChatService;
import com.cbio.core.service.AttendantService;
import com.cbio.core.service.SessaoService;
import com.cbio.core.v1.dto.AttendantDTO;
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

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitAttendantTelegramAction implements Action {

    private final SessaoService sessaoService;
    private final AttendantService attendantService;
    private final ChatService chatService;

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
//            simpMessagingTemplate
//                    .convertAndSend("/topic/demo", "HoOoooopppeee !! ");
            SessaoEntity sessaoEntity = sessaoService
                    .validaOuCriaSessaoAtivaPorUsuarioCanal(
                            Long.valueOf(senderId),
                            "TELEGRAM",
                            System.currentTimeMillis());

            try {
                AttendantDTO attendantDTO = attendantService.fetch();
                sessaoEntity.setAtendimentoAberto(Boolean.TRUE);
                sessaoEntity.setUlitmoAtendente(attendantDTO);
                sessaoService.salva(sessaoEntity);

                ChatChannelInitializationDTO chatChannelInitialization = ChatChannelInitializationDTO.builder()
                        .userIdOne(attendantDTO.getId())
                        .userIdTwo(senderId)
                        .build();

                String channelUuid = chatService.establishChatSession(chatChannelInitialization);

                //TODO disparar websocket para lista de atendimentos
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