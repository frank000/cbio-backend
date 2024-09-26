package com.cbio.app.service;

import com.cbio.app.entities.CanalEntity;
import com.cbio.app.service.mapper.CanalMapper;
import com.cbio.app.service.mapper.CycleAvoidingMappingContext;
import com.cbio.app.service.minio.MinioService;
import com.cbio.core.service.*;
import com.cbio.core.v1.dto.EntradaMensagemDTO;
import com.cbio.core.v1.dto.MediaDTO;
import com.cbio.core.v1.dto.WebhookEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatsapp.api.WhatsappApiFactory;
import com.whatsapp.api.configuration.ApiVersion;
import com.whatsapp.api.domain.media.MediaFile;
import com.whatsapp.api.domain.messages.ReadMessage;
import com.whatsapp.api.domain.messages.type.MessageType;
import com.whatsapp.api.domain.response.Response;
import com.whatsapp.api.domain.webhook.Image;
import com.whatsapp.api.domain.webhook.Message;
import com.whatsapp.api.domain.webhook.WebHookEvent;
import com.whatsapp.api.impl.WhatsappBusinessCloudApi;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.RequestBody;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

@Service
@Slf4j
@Data
public class WhatsappServiceImpl implements WhatsappService {

    private final UsuarioTelegramService usuarioService;

    private final MensagemService mensagemService;

    private final SessaoService sessaoService;

    private final RespostaService respostaService;

    private final CanalService canalService;

    private final CanalMapper canalMapper;

    private final MinioService minioService;

    private final ChatbotForwardService forwardService;

    @Value("${telegram.url}")
    private String url;

    @Value("${telegram.endpoint.send.message}")
    private String endpointSendMessage;

    Logger logger = Logger.getLogger(TelegramService.class.getName());

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void connectToBot(Update update, CanalEntity canal) throws Exception {

    }

    @Override
    public Object sendMessage(RequestBody body, CanalEntity canal) throws IOException {
        return null;
    }

    @Override
    public void processaMensagem(EntradaMensagemDTO entradaMensagemDTO, CanalEntity canalEntity) {
        try {
            forwardService.processaMensagem(entradaMensagemDTO);
        } catch (Exception e) {
            String msg = String.format("Exceção: %s", e.getMessage());
            throw new RuntimeException(msg);
        }
    }

    public void markMessageAsRead(String waid, CanalEntity canalEntity){
        WhatsappApiFactory factory = WhatsappApiFactory.newInstance(canalEntity.getApiKey());
        WhatsappBusinessCloudApi whatsappBusinessCloudApi = factory.newBusinessCloudApi(ApiVersion.V20_0);

        Response response = whatsappBusinessCloudApi.markMessageAsRead(canalEntity.getIdCanal(), new ReadMessage(waid));
        log.info(response.toString());
    }
    private String getUrlMediaById(String id, CanalEntity canal) {

        WhatsappApiFactory factory = WhatsappApiFactory.newInstance(canal.getApiKey());
        WhatsappBusinessCloudApi whatsappBusinessCloudApi = factory.newBusinessCloudApi(ApiVersion.V20_0);

        return whatsappBusinessCloudApi.retrieveMediaUrl(id).url();
    }

    public MediaFile getMediaById(String url, String token) {

        WhatsappApiFactory factory = WhatsappApiFactory.newInstance(token);
        WhatsappBusinessCloudApi whatsappBusinessCloudApi = factory.newBusinessCloudApi(ApiVersion.V20_0);

        return whatsappBusinessCloudApi.downloadMediaFile(url);
    }

    @Override
    public void processEvent(String token, WebHookEvent event) throws Exception {
        AtomicReference<String> displayPhoneNumber = new AtomicReference<>();
        AtomicReference<String> identificadorRementente = new AtomicReference<>();
        AtomicReference<String> mensagem = new AtomicReference<>();
        AtomicReference<String> mensagemId = new AtomicReference<>();
        AtomicReference<String> typeMessage = new AtomicReference<>();
        AtomicReference<MediaDTO> media = new AtomicReference<>();

        boolean isMessageEvent = !event.entry().isEmpty() && event.entry().get(0).changes().get(0).value().statuses() == null;

        if(isMessageEvent){

            CanalEntity canalEntity = recoveryCanalEntity(token, event, displayPhoneNumber);


            event.entry().get(0).changes().stream()
                    .filter(change -> WebhookEvent.Contact.hasContact(change.value()))
                    .filter(change -> change.value().messages().stream().findFirst().isPresent())
                    .findFirst()
                    .ifPresent(change -> {

                        typeMessage.set(change.value().messages().stream().findFirst().get().type().name());
                        change.value().contacts().forEach(contact -> identificadorRementente.set(contact.waId()));

                        change.value().messages().forEach(message -> mensagemId.set(message.id()));

                        if(MessageType.INTERACTIVE.equals(change.value().messages().stream().findFirst().get().type()) ||
                                MessageType.TEXT.equals(change.value().messages().stream().findFirst().get().type())){
                            change.value().messages().forEach(message -> mensagem.set(getMessageBody(message)));
                        }else if(MessageType.IMAGE.equals(change.value().messages().stream().findFirst().get().type())){
                            Image image = change.value().messages().stream().findFirst().get().image();

                            media.set(MediaDTO.builder()
                                    .id(image.id())
                                    .caption(image.caption())
                                    .mimeType(image.mimeType())
                                    .mediaType(MessageType.IMAGE.name())
                                    .build());
                        }
                    });

            if(media.get() != null){
                MediaFile mediaById = getMediaById(getUrlMediaById(media.get().getId(), canalEntity), canalEntity.getApiKey());

                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                        mediaById.fileName(),
                        mediaById.fileName(),
                        media.get().getMimeType(),
                        mediaById.content());
                minioService.putFile(mockMultipartFile, media.get().getId(), canalEntity.getId() );
            }


            EntradaMensagemDTO entradaMensagemDTO = EntradaMensagemDTO
                    .builder()
                    .mensagem(ObjectUtils.defaultIfNull(mensagem.get(), null))
                    .identificadorRemetente(identificadorRementente.get())
                    .media(ObjectUtils.defaultIfNull(media.get(), null))
                    .uuid(mensagemId.get())
                    .type(typeMessage.get())
                    .canal(canalMapper.canalEntityToCanalDTO(canalEntity, new CycleAvoidingMappingContext()))
                    .build();

            processaMensagem(entradaMensagemDTO, canalEntity);
            markMessageAsRead(mensagemId.get(), canalEntity);
        }

    }

    private CanalEntity recoveryCanalEntity(String token, WebHookEvent event, AtomicReference<String> displayPhoneNumber) throws Exception {
        displayPhoneNumber.set(event.entry().get(0).changes().get(0).value().metadata().displayPhoneNumber());
        CanalEntity canalEntity = canalService.findCanalByTokenAndCliente(token, displayPhoneNumber.get())
                .orElseThrow(()->new RuntimeException("Canal não encontrado"));
        return canalEntity;
    }

    private String getMessageBody(Message message) {
        boolean notHasButton = message.interactive() == null;
        if(notHasButton){
            return message.text().body();
        }else{
            return message.interactive().buttonReply().id();
        }
    }
}
