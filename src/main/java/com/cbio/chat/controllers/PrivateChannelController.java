package com.cbio.chat.controllers;

import com.cbio.app.exception.CbioException;
import com.cbio.chat.dto.ChatChannelInitializationDTO;
import com.cbio.chat.http.JSONResponseHelper;
import com.cbio.chat.services.ChatService;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.EntradaMensagemDTO;
import com.cbio.core.v1.dto.outchatmessages.AttendantMessageOutDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/private-channel")
@Slf4j
@RequiredArgsConstructor
public class PrivateChannelController {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    @PostMapping("/send-file")
    public ResponseEntity<String> sendFile(
            @RequestPart(value = "jsonDto") final String jsonDto,
            @RequestPart(value = "file") MultipartFile file) throws Exception {


        AttendantMessageOutDTO attendantMessageOutDTO = objectMapper.readValue(jsonDto, AttendantMessageOutDTO.class);

        if (!StringUtils.hasText(attendantMessageOutDTO.getChannelId())) {
            throw new CbioException("Canal não informado. Favor reinicar o chat.", HttpStatus.BAD_REQUEST.value());
        }
        ChatChannelInitializationDTO chatChannelInitializationDTO = chatService.getChatChannelInitializationDTO(attendantMessageOutDTO.getChannelId());
        EntradaMensagemDTO entradaMensagemDTO = EntradaMensagemDTO
                .builder()
                .mensagem(null)
                .canal(
                        CanalDTO.builder()
                                .nome(chatChannelInitializationDTO.getInitCanal())
                                .build()
                )
                .type(attendantMessageOutDTO.getType())
                .identificadorRemetente(attendantMessageOutDTO.getFromUserId())
                .file(file)
                .build();

        chatService.receiveMessageAttendant(entradaMensagemDTO, attendantMessageOutDTO.getChannelId(), attendantMessageOutDTO.getToUserId());

        return JSONResponseHelper.createResponse(null, HttpStatus.OK);
    }
}
