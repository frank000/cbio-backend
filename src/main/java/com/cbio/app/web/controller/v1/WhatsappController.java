package com.cbio.app.web.controller.v1;

import com.cbio.app.meta.facebook.dto.FacebookToken;
import com.cbio.core.service.WhatsappService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatsapp.api.domain.webhook.WebHook;
import com.whatsapp.api.domain.webhook.WebHookEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

    @PostMapping("exchange-token")
    ResponseEntity<Object> exchangeToken(
            @RequestBody String code
    ){
        RestTemplate restTemplate = new RestTemplate();
        try {
            ObjectMapper mapper = new ObjectMapper();
            FacebookToken.CodeResponse response = mapper.readValue(code, FacebookToken.CodeResponse.class);

            System.out.println("Code: " + response.getCode());

            // Configurar cabeçalhos
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Parâmetros no corpo
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", "1534300350607347");
            params.add("client_secret", "a07538dc2fb8816618e2467bb17fd66c");
//            params.add("redirect_uri", "https://pleasing-elf-instantly.ngrok-free.app/");
            params.add("code", response.getCode());
            params.add("grant_type", "authorization_code");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);


            try {
                ResponseEntity<FacebookToken.FacebookTokenResponsev> responseFinal = restTemplate.postForEntity(
                        "https://graph.facebook.com/v22.0/oauth/access_token",
                        entity,
                        FacebookToken.FacebookTokenResponsev.class
                );

                return ResponseEntity.ok(responseFinal.getBody());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }


}
