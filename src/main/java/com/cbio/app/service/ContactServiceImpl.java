package com.cbio.app.service;

import com.cbio.app.entities.CompanyConfigEntity;
import com.cbio.app.entities.ContactEntity;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.CompanyConfigRepository;
import com.cbio.app.repository.ContactRepository;
import com.cbio.app.service.enuns.AssistentEnum;
import com.cbio.app.service.enuns.CanalSenderEnum;
import com.cbio.app.service.mapper.ContactMapper;
import com.cbio.app.service.utils.VariablesUtil;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.service.*;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.ContactDTO;
import com.cbio.core.v1.dto.ModelDTO;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final AuthService authService;
    private final CompanyConfigRepository companyConfigRepository;
    private final ModelService modelService;
    private final CanalService canalService;
    private final VariablesUtil variablesUtil;
    private final SessaoService sessaoService;
    private final ChatbotForwardService chatbotForwardService;


    @Override
    public ContactDTO save(ContactDTO contactDTO) throws CbioException {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        if(StringUtils.isEmpty(companyIdUserLogged) && contactDTO.getCompany() != null) {
            companyIdUserLogged = contactDTO.getCompany().getId();
        }
        if (StringUtils.hasText(companyIdUserLogged)) {



            contactDTO.setCompany(
                    CompanyDTO.builder()
                            .id(companyIdUserLogged)
                            .build()
            );
            if(StringUtils.hasText(contactDTO.getPhone())){
                String normalizedPhone = normalizePhone(contactDTO.getPhone());
                contactDTO.setPhone(normalizedPhone);

            }

            contactDTO.setActive(Boolean.TRUE);
            ContactEntity entity = contactMapper.toEntity(contactDTO);
            ContactEntity saved = contactRepository.save(entity);

            if(StringUtils.hasText(entity.getPhone())){
                sendBusinessCard(companyIdUserLogged, entity);
            }


            return contactMapper.toDto(saved);

        } else {

            throw new CbioException("Companhia não encontrada", HttpStatus.NO_CONTENT.value());
        }
    }

    private void sendBusinessCard(String companyIdUserLogged, ContactEntity entity) throws CbioException {
        CompanyConfigEntity companyConfigEntity = companyConfigRepository.findByCompanyId(companyIdUserLogged)
                .orElseThrow(() -> new NotFoundException("Configuração não encontrada."));

        if(Boolean.TRUE.equals(companyConfigEntity.getAutoSend())) {
            ModelDTO model = modelService.getByName(companyConfigEntity.getModel());

            CanalDTO canal = canalService.getCanalByCompanyIdAndNome(companyIdUserLogged, CanalSenderEnum.WHATSAPP.name());

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
                    .variables(VariablesUtil.BusinessCard.populateVariablesToParameters(entity.getEmail(), model.getCompany(), sessaoEntity))
                    .build();
            log.info("WHATSAPP BUSINESS CARD: phone {} - model {}", phoneBrazilianPrefix, companyConfigEntity.getModel());

            chatbotForwardService.enviaRespostaDialogoPorCanal(canal, dialogoDTO);
        }
    }

    @Override
    public ContactDTO update(ContactDTO contactDTO) throws CbioException {
        ContactEntity entity = getContactEntity(contactDTO.getId());

        contactMapper.fromDto(contactDTO, entity);
        return contactMapper.toDto(contactRepository.save(entity));

    }

    private ContactEntity getContactEntity(String id) throws CbioException {
        ContactEntity entity = contactRepository.findById(id)
                .orElseThrow(() -> new CbioException("Contato não encontrado", HttpStatus.NO_CONTENT.value()));
        return entity;
    }

    @Override
    public ContactDTO getContact(String id) throws CbioException {
        return contactMapper.toDto(getContactEntity(id));
    }

    @Override
    public List<ContactDTO> getContacts() throws CbioException {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        if (StringUtils.hasText(companyIdUserLogged)) {
            return contactMapper.toDto(contactRepository.findByCompanyIdAndAppCreatedIsFalse(companyIdUserLogged));
        } else {
            throw new CbioException("Companhia não encontrada", HttpStatus.NO_CONTENT.value());
        }

    }

    @Override
    public void delete(String id) throws CbioException {
        ContactEntity contactEntity = getContactEntity(id);
        contactRepository.delete(contactEntity);
    }

    @NotNull
    private String normalizePhone(String phone) {
        return phone.replaceAll("\\D", "");
    }
}
