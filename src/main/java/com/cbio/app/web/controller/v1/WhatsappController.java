package com.cbio.app.web.controller.v1;

import com.cbio.app.service.minio.MinioService;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.service.DialogoService;
import com.cbio.core.service.WhatsappService;
import com.whatsapp.api.domain.webhook.WebHook;
import com.whatsapp.api.domain.webhook.WebHookEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/whatsapp")
@RequiredArgsConstructor
public class WhatsappController {


    private static final Logger log = LoggerFactory.getLogger(WhatsappController.class);
    private final WhatsappService whatsappService;
    private final DialogoService dialogoService;
    private final MinioService minioService;

    @PostMapping(value = "/webhook/{token}")
    ResponseEntity<Void> webhook(
            @RequestBody String payload,
            @PathVariable String token

    ) throws Exception {
        WebHookEvent event = WebHook.constructEvent(payload);

        whatsappService.processEvent(token, event);
        return ResponseEntity.ok().build();
    }


    @GetMapping("media-by-dialog/{dialogId}")
    public ResponseEntity<InputStreamResource>  getMedia(@PathVariable String dialogId) {
        DialogoDTO dialogoDTO = dialogoService.getById(dialogId);
        try {

            ResponseEntity<InputStreamResource> file = minioService.getFile(dialogoDTO.getMedia().getId(), dialogoDTO.getCanal().getId());
            // Retorna a imagem com o MIME type correto
            return file;
        } catch (Exception e) {
            log.error("Erro ao baixar a mídia", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
