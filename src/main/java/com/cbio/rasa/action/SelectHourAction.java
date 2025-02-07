package com.cbio.rasa.action;

import com.cbio.core.service.CalendarGoogleService;
import com.cbio.core.service.SessaoService;
import io.github.jrasa.Action;
import io.github.jrasa.CollectingDispatcher;
import io.github.jrasa.domain.Domain;
import io.github.jrasa.event.Event;
import io.github.jrasa.exception.RejectExecuteException;
import io.github.jrasa.message.Message;
import io.github.jrasa.tracker.Tracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SelectHourAction implements Action {


    private final SessaoService sessaoService;
    private final CalendarGoogleService calendarGoogleService;

    @Override
    public String name() {
        return "acao_horario_escolhida";
    }

    @Override
    public List<? extends Event> run(CollectingDispatcher dispatcher, Tracker tracker, Domain domain) throws RejectExecuteException {
        log.info("ACTION: acao_horario_escolhida");
        String text = "Selecione um horário";
        Message.MessageBuilder messageBuilder = Message
                .builder();
        if (tracker.getCurrentState() != null && tracker.getCurrentState().getSenderId() != null) {
//
//            String[] idUsuarioAndIdCanal = tracker.getCurrentState().getSenderId().split("_");
//
//            SessaoEntity sessaoEntity = sessaoService
//                    .buscaSessaoAtivaPorIdentificadorUsuario(Long.valueOf(idUsuarioAndIdCanal[0]), idUsuarioAndIdCanal[1]);
//            String companyId = sessaoEntity.getCanal().getCompany().getId();
//            try {
//                EventDTO eventDTO = EventDTO.builder()
//                        .build();
//
//                calendarGoogleService.insertEvent();
////                String resourceId = "672c2781ae950201dedb39f7";
////                QueryFilterCalendarDTO queryFilterCalendarDTO = QueryFilterCalendarDTO.builder()
////                        .startStr("2024-12-29T00:00:00-03:00")
////                        .endStr("2025-02-09T00:00:00-03:00")
////                        .resourceId(resourceId)
////                        .build();
////                Map<String, List<ScheduleDTO>> stringListMap = calendarGoogleService.listScheduleByResource(resourceId, queryFilterCalendarDTO, companyId);
////                stringListMap.isEmpty();
////                List<Button> buttons = new ArrayList<>();
////
////                stringListMap.forEach((calendar, schedules) -> {
////                    buttons.add(new Button(calendar, String.format("/escolher_hora{\"horario\": \"%s\"}", calendar)));
////                });
////                messageBuilder.buttons(buttons);
//            } catch (Exception e) {
//                text = e.getMessage();
//            }


            dispatcher
                    .utterMessage(messageBuilder
                            .text("Informe o nome completo.")
                            .build());
        }

        return Action.empty();
    }


}