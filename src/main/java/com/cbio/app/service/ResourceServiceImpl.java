package com.cbio.app.service;

import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.ResourceEntity;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.EventRepository;
import com.cbio.app.repository.ResourceCustomRepository;
import com.cbio.app.repository.ResourceRepository;
import com.cbio.app.service.enuns.AssistentEnum;
import com.cbio.app.service.enuns.CanalSenderEnum;
import com.cbio.app.service.mapper.ResourceMapper;
import com.cbio.app.service.utils.EventoUtil;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.service.*;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.ModelDTO;
import com.cbio.core.v1.dto.ResourceDTO;
import com.cbio.core.v1.dto.google.EventDTO;
import com.cbio.core.v1.dto.notification.NotificationJobDTO;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    private final ResourceCustomRepository resourceCustomRepository;
    private final AuthService authService;
    private final ResourceMapper resourceMapper;
    private final SessaoService sessaoService;
    private final CanalService canalService;
    private final ModelService modelService;
    private final ChatbotForwardService forwardService;
    private final EventoUtil eventoUtil;
    private final EventRepository eventRepository;


    public void notifyByConfigNotification() {
        List<NotificationJobDTO> eventsToNotify = resourceCustomRepository.getEventsToNotify();

        eventsToNotify
                .forEach(notificationJobDTO -> {
                    try {
                        ResourceEntity.NotificationDTO configNotification = notificationJobDTO.getNotificationByCanal(CanalSenderEnum.WHATSAPP)
                                .orElseThrow(() -> new CbioException("Notificação não configurada.", HttpStatus.NO_CONTENT.value()));

                        ModelDTO model = modelService.getByName(configNotification.getModel());


                        LocalDateTime now = CbioDateUtils.LocalDateTimes.now();
                        LocalDateTime nowPlusVerification = now.plusSeconds(configNotification.getAntecedence());
                        notificationJobDTO
                                .getEvents()
                                .stream()
                                .filter(eventDTO -> StringUtils.hasText(eventDTO.getPhone()))
                                .filter(eventDTO -> eventDTO.getStartDate().isBefore(nowPlusVerification) && eventDTO.getStartDate().isAfter(now))
                                .forEach(eventDTO -> {

                                    try {
                                        CanalDTO canal = canalService.getCanalByCompanyIdAndNome(notificationJobDTO.getCompany().getId(), CanalSenderEnum.WHATSAPP.name());

                                        String phoneBrazilianPrefix = eventoUtil.handleAndGetPhoneNumber(eventDTO.getPhone());

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
                                                .variables(eventoUtil.populateVariablesToParametersEvent(eventDTO.getEmail(), notificationJobDTO.getCompany(), eventDTO, sessaoEntity))
                                                .build();
                                        log.info("WHATSAPP NOTIFY: {} - {}", eventDTO.getTitle(), eventDTO.getStartDate());

                                        DialogoDTO dialogoDTO1 = forwardService.enviaRespostaDialogoPorCanal(canal, dialogoDTO);

                                        alterNotifiedEvent(eventDTO);

                                    } catch (CbioException e) {
                                        throw new RuntimeException(e);
                                    }

                                });


                    } catch (CbioException e) {
                        throw new BadRequestException(e.getMessage());
                    }
                });

    }

    private void alterNotifiedEvent(EventDTO eventDTO) {
        eventRepository.findById(eventDTO.getId()).ifPresent(event -> {
            event.setNotified(Boolean.TRUE);
            eventRepository.save(event);
        });
    }


    @Override
    public ResourceDTO save(ResourceDTO dto) {

        validateForm(dto);

        if (authService.getClaimsUserLogged().get("companyId") != null) {
            String companyId = authService.getClaimsUserLogged().get("companyId").toString();

            dto.setCompany(CompanyDTO.builder()
                    .id(companyId)
                    .build());
            dto.setActive(Boolean.TRUE);
            dto.setStatus(ResourceEntity.StatusEnum.SYNC);
            ResourceEntity entity = resourceMapper.toEntity(dto);


            return resourceMapper.toDto(resourceRepository.save(entity));
        } else {
            throw new RuntimeException("Você não está logado");
        }
    }

    private static void validateForm(ResourceDTO dto) {
        if (ObjectUtils.isEmpty(dto.getColor())) {
            throw new BadRequestException("Cor de fundo é obrigatório.");
        }
        if (!CollectionUtils.isEmpty(dto.getNotifications())) {
            boolean notHasAntecedence = dto.getNotifications().stream()
                    .anyMatch(notificationDTO -> notificationDTO.getAntecedence() == null);

            boolean notHasModel = dto.getNotifications().stream()
                    .anyMatch(notificationDTO -> !StringUtils.hasText(notificationDTO.getModel()));

            boolean notHasChannel = dto.getNotifications().stream()
                    .anyMatch(notificationDTO -> ObjectUtils.isEmpty(notificationDTO.getChannel()));

            if (notHasChannel) {
                throw new BadRequestException("Canal é obrigatório.");
            }
            if (notHasAntecedence) {
                throw new BadRequestException("Antecedência é obrigatório.");
            }
            if (notHasModel) {
                throw new BadRequestException("Modelo é obrigatório.");
            }
        }
    }

    @Override
    public ResourceDTO update(ResourceDTO dto) {
        validateForm(dto);

        ResourceEntity entity = resourceRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Recurso não encontrado."));
        dto.setStatus(ResourceEntity.StatusEnum.SYNC);
        resourceMapper.fromDto(dto, entity);
        return resourceMapper.toDto(resourceRepository.save(entity));
    }

    @Override
    public ResourceDTO getResourceById(String resourceId) {
        ResourceEntity entity = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Recurso não encontrado."));
        return resourceMapper.toDto(entity);
    }

    @Override
    public void delete(String id) {
        ResourceEntity entity = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Recurso não encontrado."));
        entity.setActive(Boolean.FALSE);
        resourceRepository.save(entity);
    }

    public List<ResourceDTO> getResourceFilterSelection() {
        if (authService.getClaimsUserLogged().get("companyId") != null) {
            String companyId = authService.getClaimsUserLogged().get("companyId").toString();

            List<ResourceEntity> resourcesByCompanyId = resourceRepository.getResourcesByCompanyId(companyId);

            return resourceMapper.toDto(resourcesByCompanyId);
        } else {
            throw new RuntimeException("Você não está logado");
        }
    }

    public Optional<ResourceDTO> getResourceByCompanyAndDairyName(String dairyName ) {
        if (authService.getClaimsUserLogged().get("companyId") != null) {
            String companyId = authService.getClaimsUserLogged().get("companyId").toString();

            Optional<ResourceEntity> resourceEntity = resourceRepository.getResourceByCompanyIdAndDairyNameIgnoreCase(companyId, dairyName);

            return resourceEntity.map(resourceMapper::toDto);

        } else {
            throw new RuntimeException("Você não está logado");
        }
    }

    public Optional<ResourceDTO> getResourceByCompanyAndDairyNameAndCompanyId(String dairyName, String companyId) {
        Optional<ResourceEntity> resourceEntity = resourceRepository.getResourceByCompanyIdAndDairyNameIgnoreCase(companyId, dairyName);

        return resourceEntity.map(resourceMapper::toDto);
    }

    @Override
    public void saveResourceByDairyName(String dairyName, String companyId) {
        ResourceEntity entity = ResourceEntity.builder()
                .dairyName(dairyName)
                .active(Boolean.TRUE)
                .status(ResourceEntity.StatusEnum.DESYNC)
                .company(
                        CompanyDTO.builder()
                                .id(companyId)
                                .build()
                )
                .build();

        resourceRepository.save(entity);
    }
}
