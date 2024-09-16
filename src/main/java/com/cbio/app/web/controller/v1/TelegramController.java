package com.cbio.app.web.controller.v1;

import com.cbio.app.service.mapper.CanalMapper;
import com.cbio.app.service.mapper.CycleAvoidingMappingContext;
import com.cbio.app.web.SecuredRestController;
import com.cbio.core.v1.dto.EntradaMensagemDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cbio.app.entities.CanalEntity;
import com.cbio.core.service.CalendarGoogleService;
import com.cbio.core.service.CanalService;
import com.cbio.core.service.TelegramService;
import com.cbio.core.v1.dto.GitlabEventDTO;
import com.cbio.core.v1.dto.MensagemDto;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/v1/bot")
@RequiredArgsConstructor
public class TelegramController implements SecuredRestController {


    private final TelegramService service;
    private final CanalService canalService;
    private final ObjectMapper objectMapper;
    private final CalendarGoogleService calendarService;
    private final CanalMapper canalMapper;

    @PostMapping(value = "/webhook")
    @PreAuthorize("@validacaoCallbackService.validaToken('TELEGRAM', #token)")
    ResponseEntity<Void> webhook(
            @RequestParam("token") String token,
            @RequestParam("cliente") String cliente,
            @org.springframework.web.bind.annotation.RequestBody Update update

    ) throws Exception {
        CanalEntity canalEntity = canalService.findCanalByTokenAndCliente(token, cliente)
                .orElseThrow(()->new RuntimeException("Canal não encontrado"));

        boolean temMensagemParaProcessar = update.getMessage() != null || update.getCallbackQuery() != null;

        if (temMensagemParaProcessar){
            EntradaMensagemDTO entradaMensagemDTO = EntradaMensagemDTO
                    .builder()
                    .mensagem(update.getMessage() != null ? update.getMessage().getText() : update.getCallbackQuery().getData())
                    .mensagemObject(ObjectUtils.defaultIfNull(update, null))
                    .canal(canalMapper.canalEntityToCanalDTO(canalEntity, new CycleAvoidingMappingContext()))
                    .build();

            service.processaMensagem(entradaMensagemDTO, canalEntity);
        }


        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/connect")
    @PreAuthorize("@validacaoCallbackService.validaToken('TELEGRAM', #token)")
    ResponseEntity<Void> connect(
            @RequestParam("token") String token,
            @RequestParam("cliente") String cliente,
            @org.springframework.web.bind.annotation.RequestBody Update update

    ) throws Exception {
        CanalEntity canalEntity = canalService.findCanalByTokenAndCliente(token, cliente)
                .orElseThrow(()->new RuntimeException("Canal não encontrado"));

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
