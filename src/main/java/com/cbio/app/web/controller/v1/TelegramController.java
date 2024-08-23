package com.cbio.app.web.controller.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cbio.app.entities.CanalEntity;
import com.cbio.core.service.CalendarGoogleService;
import com.cbio.core.service.CanalService;
import com.cbio.core.service.TelegramService;
import com.cbio.core.v1.dto.GitlabEventDTO;
import com.cbio.core.v1.dto.MensagemDto;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@RestController
@RequestMapping("/v1/bot")
public class TelegramController {


    TelegramService service;
    CanalService canalService;
    ObjectMapper objectMapper;
    CalendarGoogleService calendarService;

    public TelegramController(TelegramService service, CanalService canalService, ObjectMapper objectMapper, CalendarGoogleService calendarService1) {
        this.service = service;
        this.canalService = canalService;
        this.objectMapper = objectMapper;
        this.calendarService = calendarService1;
    }
    @PostMapping(value = "/webhook")
    @PreAuthorize("@validacaoCallbackService.validaToken('TELEGRAM', #token)")
    ResponseEntity<Void> webhook(
            @RequestParam("token") String token,
            @RequestParam("cliente") String cliente,
            @org.springframework.web.bind.annotation.RequestBody Update update

    ) throws Exception {
        CanalEntity canalEntity = canalService.findCanalByTokenAndCliente(token, cliente);

        service.processaMensagem(update, canalEntity);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/connect")
    @PreAuthorize("@validacaoCallbackService.validaToken('TELEGRAM', #token)")
    ResponseEntity<Void> connect(
            @RequestParam("token") String token,
            @RequestParam("cliente") String cliente,
            @org.springframework.web.bind.annotation.RequestBody Update update

    ) throws Exception {
        CanalEntity canalEntity = canalService.findCanalByTokenAndCliente(token, cliente);

        service.connectToBot(update, canalEntity);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/enviar-mensagem")
    Object sendMessage(@org.springframework.web.bind.annotation.RequestBody MensagemDto mensagem, CanalEntity canal) throws IOException {

        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = okhttp3.RequestBody.create(mediaType, mensagem.getEnvio());

        return service.sendMessage(body, canal);
    }


    @PostMapping(value = "/gitlab-alert")
    @PreAuthorize("@validacaoCallbackService.validaToken('TELEGRAM', #token)")
    public ResponseEntity<Void> receiveWebhhokGitlab(
            @RequestParam("token") String token,
            @RequestParam(value = "cliente", required = false) String cliente,
            @org.springframework.web.bind.annotation.RequestBody Object event

    ) throws Exception {

        GitlabEventDTO obj = GitlabEventDTO
                .builder()
                .event(objectMapper.convertValue(event, new TypeReference<Map<String, Object>>() {
                }))
                .build();

        service.enviaMenssagemParaGrupo(token, cliente, obj);

        return ResponseEntity.ok().build();
    }

    @GetMapping("teste/{id}")
    public void teste(@PathVariable String id) throws GeneralSecurityException, IOException {
        calendarService.executa(id);
    }

}
