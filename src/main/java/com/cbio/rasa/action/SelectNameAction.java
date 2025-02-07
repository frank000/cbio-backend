package com.cbio.rasa.action;

import com.cbio.core.service.CalendarGoogleService;
import com.cbio.core.service.SessaoService;
import io.github.jrasa.Action;
import io.github.jrasa.CollectingDispatcher;
import io.github.jrasa.domain.Domain;
import io.github.jrasa.event.Event;
import io.github.jrasa.event.SlotSet;
import io.github.jrasa.exception.RejectExecuteException;
import io.github.jrasa.message.Message;
import io.github.jrasa.tracker.Tracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SelectNameAction implements Action {


    private final SessaoService sessaoService;
    private final CalendarGoogleService calendarGoogleService;

    @Override
    public String name() {
        return "action_receber_nome";
    }

    @Override
    public List<? extends Event> run(CollectingDispatcher dispatcher, Tracker tracker, Domain domain) throws RejectExecuteException {
        log.info("ACTION: action_receber_nome");
        String text = "Selecione um horário";

        String nome = tracker.getLatestMessage().getText();

        Message.MessageBuilder messageBuilder = Message
                .builder();

            dispatcher
                    .utterMessage(messageBuilder
                            .text("Informe o e-mail ou whatsapp.")
                            .build());

        return Collections.singletonList(
                new SlotSet("nome", nome) // 👈 Define o slot corretamente
        );
    }


}