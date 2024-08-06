package com.policia.df.bot.app.web.controller.v1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.policia.df.bot.app.entities.CanalEntity;
import com.policia.df.bot.core.service.CanalService;
import com.policia.df.bot.core.service.TelegramService;
import com.policia.df.bot.core.v1.dto.GitlabEventDTO;
import com.policia.df.bot.core.v1.dto.MensagemDto;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/v1/bot")
public class TelegramController {


    TelegramService service;
    CanalService canalService;
    ObjectMapper objectMapper;

    public TelegramController(TelegramService service, CanalService canalService, ObjectMapper objectMapper) {
        this.service = service;
        this.canalService = canalService;
        this.objectMapper = objectMapper;
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



}
