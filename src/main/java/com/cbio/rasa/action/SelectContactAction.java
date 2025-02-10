package com.cbio.rasa.action;

import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.ResourceEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.GoogleCredentialRepository;
import com.cbio.app.repository.ResourceRepository;
import com.cbio.core.service.CalendarGoogleService;
import com.cbio.core.service.SessaoService;
import com.cbio.core.v1.dto.google.EventDTO;
import io.github.jrasa.Action;
import io.github.jrasa.CollectingDispatcher;
import io.github.jrasa.domain.Domain;
import io.github.jrasa.event.Event;
import io.github.jrasa.exception.RejectExecuteException;
import io.github.jrasa.message.Message;
import io.github.jrasa.tracker.Tracker;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SelectContactAction implements Action {


    private final SessaoService sessaoService;
    private final CalendarGoogleService calendarGoogleService;
    private final ResourceRepository resourceRepository;
    private final GoogleCredentialRepository googleCredentialRepository;

    @Override
    public String name() {
        return "action_receber_contato";
    }

    @Override
    public List<? extends Event> run(CollectingDispatcher dispatcher, Tracker tracker, Domain domain) throws RejectExecuteException {
        log.info("ACTION: action_receber_contato");
        String text = "Selecione um horário";

        ResourceEntity recurso = resourceRepository.findById(tracker.getSlot("recurso", String.class))
                .orElseThrow();

        String contato = tracker.getSlot("contato", String.class);
        EventDTO.EventDTOBuilder builder = EventDTO.builder();


        if(contato.contains("@") && contato.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")){
            builder.email(contato);
        }else{

            if (contato.matches(".*\\d.*")) {

                String telefone = contato.replaceAll("\\D", "");

                if (telefone.startsWith("0")) {
                    telefone = telefone.substring(1);
                }
                builder.phone(telefone);
            } else {

                dispatcher
                        .utterMessage(  Message.builder()
                                .text("Número inválido")
                                .build());

                return Action.empty();
            }
        }
        String horario = tracker.getSlot("horario", String.class);
        String[] split = horario.split("&");

        EventDTO eventDTO = builder
                .title(recurso.getTitle())
                .dairyName(recurso.getDairyName())
                .name(tracker.getSlot("nome", String.class))
                .company(recurso.getCompany())
                .start(split[0].concat(":00").concat(CbioDateUtils.MINUS_3))
                .end(split[1].concat(":00").concat(CbioDateUtils.MINUS_3))
                .appCreated(Boolean.TRUE)
                .build();

        String result = "Tente novamente.";
        try {
            calendarGoogleService.insertEvent(eventDTO, recurso.getCompany().getId());
            result = "Agendamento realizado com sucesso.";

        } catch (CbioException | ServerException | InsufficientDataException | ErrorResponseException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {

            log.error(e.getMessage(), e);
        }

            dispatcher
                    .utterMessage(Message.builder()
                            .text(result)
                            .build());
        return Action.empty();
    }


}