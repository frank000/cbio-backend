package com.cbio.app.service;

import com.cbio.app.entities.EventEntity;
import com.cbio.app.entities.ResourceEntity;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.EventRepository;
import com.cbio.app.service.enuns.AssistentEnum;
import com.cbio.app.service.enuns.CanalSenderEnum;
import com.cbio.app.service.mapper.EventMapper;
import com.cbio.app.service.utils.VariablesUtil;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.service.*;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.ModelDTO;
import com.cbio.core.v1.dto.ResourceDTO;
import io.minio.errors.*;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;


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
    private final VariablesUtil variablesUtil;


    @Override
    public void alterNotify(String id) {
        eventRepository.findById(id).ifPresent(event -> {
            event.setNotified(Boolean.TRUE);
            eventRepository.save(event);
        });
    }

    @Override
    public void notify(String id) throws CbioException, ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {

        EventEntity entity = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado."));

        ResourceDTO resourceByCompanyAndDairyName = resourceService.getResourceByCompanyAndDairyName(entity.getDairyName())
                .orElseThrow(() -> new NotFoundException("Recurso não encontrado."));


        ResourceEntity.NotificationDTO notificationDTO = resourceByCompanyAndDairyName.getNotifications().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Notificação não definida para o recurso do evento."));

        ModelDTO model = modelService.getByName(notificationDTO.getModel());

        CanalDTO canal = canalService.getCanalByCompanyIdAndNome(resourceByCompanyAndDairyName.getCompany().getId(), CanalSenderEnum.WHATSAPP.name());

        String phoneBrazilianPrefix = variablesUtil.handleAndGetPhoneNumber(entity.getPhone());

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
                .variables(VariablesUtil.Event.populateVariablesToParametersEvent(entity.getEmail(), model.getCompany(), eventMapper.toDto(entity), sessaoEntity))
                .build();
        log.info("WHATSAPP NOTIFY: {} - {}", entity.getTitle(), entity.getStartDate());

        DialogoDTO dialogoDTO1 = forwardService.enviaRespostaDialogoPorCanal(canal, dialogoDTO);

    }


}
