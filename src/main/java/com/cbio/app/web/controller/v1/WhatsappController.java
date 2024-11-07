package com.cbio.app.web.controller.v1;

import com.cbio.core.service.WhatsappService;
import com.whatsapp.api.domain.webhook.WebHook;
import com.whatsapp.api.domain.webhook.WebHookEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/whatsapp")
@RequiredArgsConstructor
public class WhatsappController {

    private final WhatsappService whatsappService;


    @PostMapping(value = "/webhook/{token}")
    ResponseEntity<Void> webhook(
            @RequestBody String payload,
            @PathVariable String token

    ) throws Exception {
        WebHookEvent event = WebHook.constructEvent(payload);

        whatsappService.processEvent(token, event);
        return ResponseEntity.ok().build();
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
