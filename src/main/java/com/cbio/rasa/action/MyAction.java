package com.cbio.rasa.action;

import io.github.jrasa.Action;
import io.github.jrasa.CollectingDispatcher;
import io.github.jrasa.domain.Domain;
import io.github.jrasa.event.Event;
import io.github.jrasa.exception.RejectExecuteException;
import io.github.jrasa.message.Message;
import io.github.jrasa.tracker.Tracker;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyAction implements Action {

    @Override
    public String name() {
        return "action_custom";
    }

    @Override
    public List<? extends Event> run(CollectingDispatcher dispatcher, Tracker tracker, Domain domain) {
        dispatcher.utterMessage(Message.builder()
                .text("Hello, World!")
                .build());
        return Action.empty();
    }
}
