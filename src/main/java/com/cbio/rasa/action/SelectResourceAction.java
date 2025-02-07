package com.cbio.rasa.action;

import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.core.service.CalendarGoogleService;
import com.cbio.core.service.SessaoService;
import com.cbio.core.v1.dto.google.QueryFilterCalendarDTO;
import com.cbio.core.v1.dto.google.ScheduleDTO;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class SelectResourceAction implements Action {


    private final SessaoService sessaoService;
    private final CalendarGoogleService calendarGoogleService;

    @Override
    public String name() {
        return "acao_recurso_escolhida";
    }

    @Override
    public List<? extends Event> run(CollectingDispatcher dispatcher, Tracker tracker, Domain domain) throws RejectExecuteException {
        log.info("ACTION: acao_recurso_escolhida");
        String text = "Seleciona a data";
        Message.MessageBuilder messageBuilder = Message
                .builder();
        if (tracker.getCurrentState() != null && tracker.getCurrentState().getSenderId() != null) {

            String[] idUsuarioAndIdCanal = tracker.getCurrentState().getSenderId().split("_");

            SessaoEntity sessaoEntity = sessaoService
                    .buscaSessaoAtivaPorIdentificadorUsuario(Long.valueOf(idUsuarioAndIdCanal[0]), idUsuarioAndIdCanal[1]);
            String companyId = sessaoEntity.getCanal().getCompany().getId();
            try {
                String resourceId = tracker.getSlot("recurso").toString();

                LocalDateTime now = CbioDateUtils.LocalDateTimes.now();
                now = now.truncatedTo(ChronoUnit.DAYS);
                String today = CbioDateUtils.LocalDateTimes.formatToIsoDateTime(now);
                String plusMonth = CbioDateUtils.LocalDateTimes.formatToIsoDateTime(now.plusMonths(1L));

                QueryFilterCalendarDTO queryFilterCalendarDTO = QueryFilterCalendarDTO.builder()
                        .startStr(today)
                        .endStr(plusMonth)
                        .resourceId(resourceId)
                        .build();

                Map<String, List<ScheduleDTO>> stringListMap = calendarGoogleService.listScheduleByResource(resourceId, queryFilterCalendarDTO, companyId);
                stringListMap.isEmpty();

                List<Button> buttons = new ArrayList<>();
                stringListMap.forEach((calendar, schedules) -> {
                    buttons.add(new Button(calendar, String.format("data escolhida %s", calendar)));
                });
                messageBuilder.buttons(buttons);
            } catch (Exception e) {
                text = e.getMessage();
            }



            dispatcher
                    .utterMessage(messageBuilder
                            .text("Selecione uma data")
                            .build());
        }


        return Action.empty();
    }


}