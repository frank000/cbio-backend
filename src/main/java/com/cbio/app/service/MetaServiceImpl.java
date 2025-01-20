package com.cbio.app.service;

import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.client.Oauth2Client;
import com.cbio.app.entities.CanalEntity;
import com.cbio.app.entities.InstagramCredentialEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.CanalRepository;
import com.cbio.app.repository.InstagramCredentialRepository;
import com.cbio.app.service.enuns.CanalSenderEnum;
import com.cbio.app.service.mapper.CanalMapper;
import com.cbio.app.service.mapper.CycleAvoidingMappingContext;
import com.cbio.core.service.CanalService;
import com.cbio.core.service.ChatbotForwardService;
import com.cbio.core.service.CompanyService;
import com.cbio.core.service.MetaService;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.EntradaMensagemDTO;
import com.cbio.core.v1.dto.MediaDTO;
import com.restfb.types.webhook.WebhookEntry;
import com.restfb.types.webhook.WebhookObject;
import com.restfb.types.webhook.messaging.MessagingAttachment;
import com.restfb.types.webhook.messaging.MessagingItem;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Service
public class MetaServiceImpl implements MetaService {


    private static final Logger log = LoggerFactory.getLogger(MetaServiceImpl.class);
    private final CanalService canalService;
    private final InstagramOauthService instagramOauthService;
    private final InstagramCredentialRepository instagramCredentialRepository;
    private final CompanyService companyService;
    private final CanalMapper canalMapper;
    private final CanalRepository canalRepository;
    private final ChatbotForwardService chatbotForwardService;

    @Value("${app.meta.instagram.client-id}")
    private String clientId;

    @Value("${app.meta.instagram.client-secret}")
    private String clientSecret;


    @Value("${app.external-url}")
    private String externalUrl;


    /**
     * @param state BY DEFAULT ITS RECEIVE COMPANY-ID AS STATES DO PRECCESS
     * @param code
     * @throws Exception
     */
    public void exchangeCodeToTokenAndSave(String state, String code) throws Exception {


        Oauth2Client.TokenResponseDTO token = instagramOauthService.getAccessToken(
                clientId,
                clientSecret,
                code,
                externalUrl + "/v1/public/meta/webhook/instagram/callback"
        );

        CanalEntity canalEntity = findOrCreateCanal(state, token.getAccessToken());
        Optional<InstagramCredentialEntity> byCompanyId = instagramCredentialRepository.findByCompanyId(canalEntity.getId());
        InstagramCredentialEntity instagramCredentialEntity;


        instagramCredentialEntity = byCompanyId.orElseGet(() -> InstagramCredentialEntity.builder()
                .build());


        Oauth2Client.TokenResponseDTO tokenResponseDTO = instagramOauthService.exchangeTokenForLongLived(clientSecret, token.getAccessToken());

        LocalDateTime nowLocalDateTime = CbioDateUtils.LocalDateTimes.now();
        LocalDateTime expirateLocalDateTime = nowLocalDateTime.plus(Duration.ofSeconds(tokenResponseDTO.getExpiresIn()));


        instagramCredentialEntity.setCompanyId(canalEntity.getId());
        instagramCredentialEntity.setExpirateTime(expirateLocalDateTime);
        instagramCredentialEntity.setCreatedTime(nowLocalDateTime);
        instagramCredentialEntity.setCredential(tokenResponseDTO);

        instagramCredentialRepository.save(instagramCredentialEntity);

        updateTokenIntoCanal(canalEntity, tokenResponseDTO);
    }

    private void updateTokenIntoCanal(CanalEntity canalEntity, Oauth2Client.TokenResponseDTO tokenResponseDTO) {
        canalEntity.setApiKey(tokenResponseDTO.getAccessToken());
        canalRepository.save(canalEntity);
    }

    private CanalEntity findOrCreateCanal(String state, String accessToken) throws Exception {
        CompanyDTO companyDTO = companyService.findById(state);
        InstagramOauthService.UserInfoMeta userInfo = instagramOauthService.getUserInfo(accessToken);
        return canalService.findCanalByNomeCompanyIdAndCliente(CanalSenderEnum.INSTAGRAM.name(), state, userInfo.getUserId())
                .orElseGet(() -> {

                    CanalDTO canalDTO = canalService.incluirCanal(
                            CanalDTO.builder()
                                    .nome(CanalSenderEnum.INSTAGRAM.name())
                                    .primeiroNome(userInfo.getUsername())
                                    .idCanal(userInfo.getId())
                                    .ativo(Boolean.TRUE)
                                    .cliente(userInfo.getUserId())
                                    .company(companyDTO)
                                    .build()
                    );
                    return canalMapper.canalDTOToCanalEntity(canalDTO, new CycleAvoidingMappingContext());
                });
    }


    public void processaMensagem(WebhookObject webhookObject) throws Exception {
        try {
            EntradaMensagemDTO entradaMensagemDTO = null;
            WebhookEntry data = webhookObject.getEntryList().stream().findFirst().orElseThrow();

            String id = data.getId();
            MessagingItem messagingItem = data.getMessaging().stream().findFirst().orElseThrow();
            AtomicReference<MessagingAttachment> mediaReference = new AtomicReference<>();
            if (id.equals(messagingItem.getSender().getId())) {
                return;
            }

            String nome = webhookObject.getObject().toString().toUpperCase();
            CanalEntity canalEntity = canalService.findCanalByNomeAndCliente(nome, id)
                    .orElseThrow(() -> new CbioException("Canal não encontrado", HttpStatus.NO_CONTENT.value()));


            AtomicReference<String> typeMessage = new AtomicReference<>();
            AtomicReference<String> message = new AtomicReference<>();
            AtomicReference<String> mid = new AtomicReference<>();

            if (messagingItem.getPostback() != null && StringUtils.hasText(messagingItem.getPostback().getPayload())){

                typeMessage.set("TEXT");
                message.set(messagingItem.getPostback().getPayload());
                mid.set(messagingItem.getPostback().getMid());

            }else if(StringUtils.hasText(messagingItem.getMessage().getText())) {

                typeMessage.set("TEXT");
                message.set(messagingItem.getMessage().getText());
                mid.set(messagingItem.getMessage().getMid());

            } else if (messagingItem.getMessage().hasAttachment()) {

                MessagingAttachment first = messagingItem.getMessage().getAttachments().stream().findFirst().get();
                mediaReference.set(first);
                typeMessage.set(first.getType().toUpperCase());
            }


            entradaMensagemDTO = EntradaMensagemDTO
                    .builder()
                    .mensagem(ObjectUtils.defaultIfNull(message.get(), null))
                    .identificadorRemetente(messagingItem.getSender().getId())
                    .media(extractMedia(mediaReference))
                    .uuid(ObjectUtils.defaultIfNull(mid.get(), null))
                    .type(typeMessage.get())
                    .canal(canalMapper.canalEntityToCanalDTO(canalEntity, new CycleAvoidingMappingContext()))
                    .build();


            chatbotForwardService.processaMensagem(entradaMensagemDTO);
        } catch (Exception e) {
            String msg = String.format("Exceção processamento mensagem: %s", e.getMessage());
            log.error(msg, e);
            throw new RuntimeException(msg);
        }
    }

    private MediaDTO extractMedia(AtomicReference<MessagingAttachment> mediaReference) {
        if (mediaReference.get() != null) {
            return MediaDTO.builder()
                    .url(mediaReference.get().getPayload().getUrl())
                    .mimeType(mediaReference.get().getType())
                    .caption(mediaReference.get().getTitle())
                    .build();

        } else {
            return null;
        }
    }
}
