package com.cbio.rasa.action;

import com.cbio.app.entities.SessaoEntity;
import com.cbio.core.service.CalendarGoogleService;
import com.cbio.core.service.ResourceService;
import com.cbio.core.service.SessaoService;
import com.cbio.core.v1.dto.ResourceDTO;
import io.github.jrasa.Action;
import io.github.jrasa.CollectingDispatcher;
import io.github.jrasa.domain.Domain;
import io.github.jrasa.event.Event;
import io.github.jrasa.exception.RejectExecuteException;
import io.github.jrasa.message.Button;
import io.github.jrasa.message.Message;
import io.github.jrasa.tracker.Tracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitScheduleAction implements Action {


    private final SessaoService sessaoService;
    private final CalendarGoogleService calendarGoogleService;
    private final ResourceService resourceService;

    @Override
    public String name() {
        return "acao_inicia_agendamento";
    }

    @Override
    public List<? extends Event> run(CollectingDispatcher dispatcher, Tracker tracker, Domain domain) throws RejectExecuteException {
        log.info("ACTION: acao_inicia_agendamento");
        String text = "Seleciona o recurso";
        Message.MessageBuilder messageBuilder = Message
                .builder();
        if (tracker.getCurrentState() != null && tracker.getCurrentState().getSenderId() != null) {

            String[] idUsuarioAndIdCanal = tracker.getCurrentState().getSenderId().split("_");

            SessaoEntity sessaoEntity = sessaoService
                    .buscaSessaoAtivaPorIdentificadorUsuario(Long.valueOf(idUsuarioAndIdCanal[0]), idUsuarioAndIdCanal[1]);

            try {

                List<ResourceDTO> allResourcesIsConfigured = resourceService.getAllResourcesIsConfigured();

                List<Button> buttons = new ArrayList<>();
                allResourcesIsConfigured
                        .stream()
                        .filter(resourceDTO -> StringUtils.hasText(resourceDTO.getDairyName()))
                        .filter(resourceDTO -> !ObjectUtils.isEmpty(resourceDTO.getSelectedDays()))
                        .forEach((dto) -> {
                    buttons.add(new Button(dto.getDairyName(), String.format("/escolher_recurso{\"recurso\": \"%s\"}", dto.getId())));
                });
                messageBuilder.buttons(buttons);
            } catch (Exception e) {
                text = e.getMessage();
            }



            dispatcher
                    .utterMessage(messageBuilder
                            .text("Selecione o recurso")
                            .build());
        }

        return Action.empty();
    }


}