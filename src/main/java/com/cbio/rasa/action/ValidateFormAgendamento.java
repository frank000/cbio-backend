package com.cbio.rasa.action;

import io.github.jrasa.Action;
import io.github.jrasa.CollectingDispatcher;
import io.github.jrasa.domain.Domain;
import io.github.jrasa.event.*;
import io.github.jrasa.exception.RejectExecuteException;
import io.github.jrasa.tracker.Tracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ValidateFormAgendamento implements Action {
    @Override
    public String name() {
        return "validate_form_agendamento";
    }

    @Override
    public List<? extends Event> run(CollectingDispatcher dispatcher, Tracker tracker, Domain domain) throws RejectExecuteException {
        List<Event> events = new ArrayList<>();

        // Verifica se o intent atual é agendamento_confirmacao
        String intent = tracker.getLatestMessage().getIntent().getName();
        if ("agendamento_confirmacao".equals(intent)) {


            // Inicia o formulário form_agendamento
            events.add(new ActiveLoop("form_agendamento"));
            // Define o primeiro slot a ser preenchido
            events.add(new SlotSet("requested_slot", "recurso"));
        }

        return events;
    }


    public class ActiveLoop extends Event {
        public static final String NAME = "active_loop";
        private final String name;

        public ActiveLoop() {
            super(NAME);
            this.name = null;
        }

        public ActiveLoop(String name) {
            super(NAME);
            this.name = name;
        }

        public ActiveLoop(Double timestamp) {
            super(NAME, timestamp);
            this.name = null;
        }

    }
}