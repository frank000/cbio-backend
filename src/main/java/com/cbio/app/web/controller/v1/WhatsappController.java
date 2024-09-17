package com.cbio.app.web.controller.v1;

import com.cbio.app.entities.CanalEntity;
import com.cbio.app.service.mapper.CanalMapper;
import com.cbio.app.service.mapper.CycleAvoidingMappingContext;
import com.cbio.core.service.CanalService;
import com.cbio.core.service.WhatsappService;
import com.cbio.core.v1.dto.EntradaMensagemDTO;
import com.cbio.core.v1.dto.WebhookEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatsapp.api.domain.webhook.Message;
import com.whatsapp.api.domain.webhook.WebHook;
import com.whatsapp.api.domain.webhook.WebHookEvent;
import com.whatsapp.api.domain.webhook.type.MessageStatus;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/v1/whatsapp")
@RequiredArgsConstructor
public class WhatsappController {


    //   public static final String token = "EAAO9LRq39UQBO3hQ1F6ALeAKA1R3NZBXiy3dKZB5XrUry807mdkZBZCnc3RBx8miH3IEdKLBHG100kTJ8gQhvxZCv3PQQp66y5jPQrO0Cn5ZCuMgrulgbdDlqwnlv2ZCjuDYmpxxee81qu7iykueBqZB52D5WZAcm3gDCC1PoeKwT16NHoOUTp55kBFnxqjHMCb3BKbH4Kbt1eOFIlEXu";
    private final WhatsappService service;
    private final CanalService canalService;
    private final CanalMapper canalMapper;

    @PostMapping(value = "/webhook/{token}")
    ResponseEntity<Void> webhook(
            @RequestBody String payload,
            @PathVariable String token

    ) throws Exception {
        WebHookEvent event = WebHook.constructEvent(payload);

        AtomicReference<String> displayPhoneNumber = new AtomicReference<>();
        AtomicReference<String> identificadorRementente = new AtomicReference<>();
        AtomicReference<String> mensagem = new AtomicReference<>();

        // WhatsappApiFactory factory = WhatsappApiFactory.newInstance(token);
        boolean isNotEvent = event.entry().get(0).changes().get(0).value().statuses() == null;

        if(!event.entry().isEmpty() && isNotEvent){
            event.entry().get(0).changes().stream()
                    .filter(change -> WebhookEvent.Change.hasMessageOrButtonAction(change.value()))
                    .filter(change -> WebhookEvent.Contact.hasContact(change.value()))
                    .findFirst()
                    .ifPresent(change -> {
                        displayPhoneNumber.set(change.value().metadata().displayPhoneNumber());
                        change.value().contacts().forEach(contact -> identificadorRementente.set(contact.waId()));
                        change.value().messages().forEach(message -> mensagem.set(getMessageBody(message)));
                    });
        }

//        event.entry().getFirst().changes().getFirst().value().messages().getFirst().interactive().buttonReply().id()



        // canal
        // nome cliente.getEntry().getFirst().getChanges().getFirst().getValue().getContacts().getFirst().profile
        // idUsuario = cliente.getEntry().getFirst().getChanges().getFirst().getValue().getContacts().getFirst().getWaId().toString()

        if(StringUtils.hasText(mensagem.get()) && StringUtils.hasText(identificadorRementente.get())) {
            CanalEntity canalEntity = canalService.findCanalByTokenAndCliente(token, displayPhoneNumber.get())
                    .orElseThrow(()->new RuntimeException("Canal não encontrado"));

            EntradaMensagemDTO entradaMensagemDTO = EntradaMensagemDTO
                    .builder()
                    .mensagem(ObjectUtils.defaultIfNull(mensagem.get(), null))
                    .identificadorRemetente(identificadorRementente.get())
                    .canal(canalMapper.canalEntityToCanalDTO(canalEntity, new CycleAvoidingMappingContext()))
                    .build();


//
            service.processaMensagem(entradaMensagemDTO, canalEntity);
        }
        return ResponseEntity.ok().build();
    }

    private static String getMessageBody(Message message) {
        boolean notHasButton = message.interactive() == null;
        if(notHasButton){
            return message.text().body();
        }else{
            return message.interactive().buttonReply().id();
        }
    }


    @GetMapping(value = "/webhook/{tokenurl}")
    ResponseEntity<String> webhooks(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challange,
            @RequestParam("hub.verify_token") String token,
            @PathVariable String tokenurl

    ) {
        System.out.println(token);
        System.out.println(challange);
        System.out.println(mode);
        System.out.println(tokenurl);
        return ResponseEntity.ok(challange);
    }


}
