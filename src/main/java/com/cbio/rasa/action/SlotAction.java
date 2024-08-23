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
public class SlotAction implements Action {

    @Override
    public String name() {
        return "action_send_info";
    }

    @Override
    public List<? extends Event> run(CollectingDispatcher dispatcher, Tracker tracker, Domain domain) throws RejectExecuteException {
        String info = (String)tracker.getSlot("matricula");
        dispatcher
                .utterMessage(Message.builder().text(String.format("O seguinto valor está correto? %s", info))
                .build());
        return Action.empty();
    }
}