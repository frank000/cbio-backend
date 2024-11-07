package com.cbio.rasa.action;

import io.github.jrasa.Action;
import io.github.jrasa.CollectingDispatcher;
import io.github.jrasa.domain.Domain;
import io.github.jrasa.event.Event;
import io.github.jrasa.event.ReminderScheduled;
import io.github.jrasa.exception.RejectExecuteException;
import io.github.jrasa.message.Message;
import io.github.jrasa.tracker.Tracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RemiderFinalizacaoAction implements Action {

    @Override
    public String name() {
        return "ac_remind_finalizacao";
    }


    @Override
    public List<? extends Event> run(CollectingDispatcher dispatcher, Tracker tracker, Domain domain) throws RejectExecuteException {
        log.info("ACTION: ac_remind_finalizacao");
        ReminderScheduled reminderScheduled = ReminderScheduled.builder("EXTERNAL_finalizacao")
                .name("remind_finalizacao")
                .triggerDateTime(LocalDateTime.now().plusSeconds(10)) // Testar com 10 segundos

                .killOnUserMessage(Boolean.TRUE)
                .build();
        dispatcher
                .utterMessage(Message.builder().text("Deseja mais alguma informação?")
                        .build());
        return List.of(reminderScheduled);
    }
}