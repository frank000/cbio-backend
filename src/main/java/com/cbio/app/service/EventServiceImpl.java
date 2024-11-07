package com.cbio.app.service;

import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.EventEntity;
import com.cbio.app.entities.ResourceEntity;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.EventRepository;
import com.cbio.app.service.enuns.AssistentEnum;
import com.cbio.app.service.enuns.CanalSenderEnum;
import com.cbio.app.service.mapper.EventMapper;
import com.cbio.app.service.utils.EventoUtil;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.service.*;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.ModelDTO;
import com.cbio.core.v1.dto.ResourceDTO;
import com.cbio.core.v1.dto.google.EventDTO;
import com.cbio.core.v1.dto.notification.NotificationJobDTO;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CanalService canalService;
    private final SessaoService sessaoService;
    private final ChatbotForwardServiceImpl forwardService;
    private final ResourceService resourceService;
    private final ModelService modelService;
    private final EventMapper eventMapper;
    private final EventoUtil eventoUtil;


    @Override
    public void alterNotify(String id) {
        eventRepository.findById(id).ifPresent(event -> {
            event.setNotified(Boolean.TRUE);
            eventRepository.save(event);
        });
    }

    @Override
    public void notify(String id) throws CbioException {

        EventEntity entity = eventRepository.findById(id)
                .orElseThrow(() -> new CbioException("Evento não encontrado.", HttpStatus.NO_CONTENT.value()));

        ResourceDTO resourceByCompanyAndDairyName = resourceService.getResourceByCompanyAndDairyName(entity.getDairyName())
                .orElseThrow(() -> new NotFoundException("Recurso não encontrado."));


        ResourceEntity.NotificationDTO notificationDTO = resourceByCompanyAndDairyName.getNotifications().stream()
                .findFirst()
                .orElseThrow(() -> new CbioException("Notificação não definida para o recurso do evento.", HttpStatus.NO_CONTENT.value()));

        ModelDTO model = modelService.getByName(notificationDTO.getModel());

        CanalDTO canal = canalService.getCanalByCompanyIdAndNome(resourceByCompanyAndDairyName.getCompany().getId(), CanalSenderEnum.WHATSAPP.name());

        String phoneBrazilianPrefix = eventoUtil.handleAndGetPhoneNumber(entity.getPhone());

        SessaoEntity sessaoEntity = sessaoService.validaOuCriaSessaoAtivaPorUsuarioCanal(
                Long.valueOf(phoneBrazilianPrefix),
                canal,
                System.currentTimeMillis()
        );


        DialogoDTO dialogoDTO = DialogoDTO.builder()
                .model(model)
                .identificadorRemetente(phoneBrazilianPrefix)
                .toIdentifier(sessaoEntity.getId())
                .canal(canal)
                .type(DialogoDTO.TypeMessageEnum.MODEL.name())
                .from(AssistentEnum.ATTENDANT.name())
//                                                .channelUuid(channelId) TODO por enquanto não precisa
                .sessionId(sessaoEntity.getId())
                .createdDateTime(LocalDateTime.now())
                .variables(eventoUtil.populateVariablesToParametersEvent(entity.getEmail(), model.getCompany(), eventMapper.toDto(entity), sessaoEntity))
                .build();
        log.info("WHATSAPP NOTIFY: {} - {}", entity.getTitle(), entity.getStartDate());

        DialogoDTO dialogoDTO1 = forwardService.enviaRespostaDialogoPorCanal(canal, dialogoDTO);

    }


}
