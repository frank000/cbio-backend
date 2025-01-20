package com.cbio.app.web.controller.v1;


import com.cbio.app.base.utils.MetaApiUtils;
import com.cbio.core.service.MetaService;
import com.restfb.types.webhook.WebhookObject;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MetaController {

    private static final Logger log = LoggerFactory.getLogger(MetaController.class);
    private final MetaService metaService;
    private final MetaApiUtils metaApiUtils;


    @PostMapping("/v1/public/meta/webhook/{channelId}/{callbackToken}")
    @PreAuthorize("@validacaoCallbackService.validaHashFacebookEToken(#signature , #facebokRawMessage, 'INSTAGRAM', #identificador, #callbackToken)")
    public ResponseEntity<Object> callback(
            @PathVariable(name = "channelId") String identificador,
            @PathVariable(name = "callbackToken") String callbackToken,
            @RequestHeader("X-Hub-Signature-256") String signature,
            @RequestBody String facebokRawMessage) throws Exception {
        WebhookObject webhookObject = metaApiUtils.toWebhookObject(facebokRawMessage);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(MetaApiUtils.RECEBIDO);
    }

    @GetMapping("/v1/public/meta/webhook/instagram/callback")
    ResponseEntity<Void> callbackLogin(
            @RequestParam(name = "state",required = false) String state,
            @RequestParam(name = "code") String code
    ) throws Exception {
        metaService.exchangeCodeToTokenAndSave(state, code);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }



    @PostMapping("/v1/public/meta/{from}")
    public ResponseEntity<Void> webhook(
            @PathVariable String from,
            @RequestHeader("X-Hub-Signature-256") String signature,
            @RequestBody String facebokRawMessage
    ) throws Exception {
        WebhookObject webhookObject = metaApiUtils.toWebhookObject(facebokRawMessage);

        metaService.processaMensagem(webhookObject);

        return ResponseEntity
                .status(HttpStatus.OK).build();
    }



    @GetMapping("/v1/public/meta/{from}")
    public ResponseEntity<String> verify(
            @RequestParam(name = "hub.verify_token") String token,
            @RequestParam(name = "hub.challenge") String challenge,
            @RequestParam(name = "hub.mode") String mode
    ) {
        if (MetaApiUtils.MODE_PERMITIDO.equals(mode)) {
            return ResponseEntity.status(HttpStatus.OK).body(challenge);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(challenge);
        }
    }

}