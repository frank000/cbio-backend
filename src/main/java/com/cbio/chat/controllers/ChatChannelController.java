package com.cbio.chat.controllers;

import com.cbio.chat.dto.ChatChannelInitializationDTO;
import com.cbio.chat.dto.ChatMessageDTO;
import com.cbio.chat.dto.EstablishedChatChannelDTO;
import com.cbio.chat.exceptions.IsSameUserException;
import com.cbio.chat.exceptions.UserNotFoundException;
import com.cbio.chat.http.JSONResponseHelper;
import com.cbio.chat.interfaces.IChatChannelController;
import com.cbio.chat.services.ChatService;
import com.cbio.chat.services.UserChatService;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.EntradaMensagemDTO;
import com.cbio.core.v1.dto.outchatmessages.AttendantMessageOutDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ChatChannelController implements IChatChannelController {
    private static final Logger log = LoggerFactory.getLogger(ChatChannelController.class);
    @Autowired
    private ChatService chatService;

    @Autowired
    private UserChatService userService;

    @Autowired
    ObjectMapper objectMapper;


    @MessageMapping("/demo.{channelId}")
    @SendTo("/topic/demo.{channelId}")
    public void receiveFromAttedant(@DestinationVariable String channelId, String message)
            throws Exception {

        ChatChannelInitializationDTO chatChannelInitializationDTO = chatService.getChatChannelInitializationDTO(channelId);
        AttendantMessageOutDTO attendantMessageOutDTO = objectMapper.readValue(message, AttendantMessageOutDTO.class);

        EntradaMensagemDTO entradaMensagemDTO = EntradaMensagemDTO
                .builder()
                .mensagem(attendantMessageOutDTO.getText().replace("\"", ""))
                .canal(
                        CanalDTO.builder()
                                .nome(chatChannelInitializationDTO.getInitCanal())
                                .build()
                )
                .type(StringUtils.hasText(message) ? "TEXT" : null)
                .identificadorRemetente(chatChannelInitializationDTO.getUserIdTwo())
                .build();

        chatService.receiveMessageAttendant(entradaMensagemDTO, channelId, chatChannelInitializationDTO.getUserIdOne());


    }
    @MessageMapping("/private.chat.{channelId}")
    @SendTo("/topic/private.chat.{channelId}")
    public ChatMessageDTO chatMessage(@DestinationVariable String channelId, ChatMessageDTO message)
            throws BeansException, UserNotFoundException {
        chatService.submitMessage(channelId, message);

        return message;
    }

    @PutMapping("/private-chat/channel")
    public ResponseEntity<String> establishChatChannel(@RequestBody ChatChannelInitializationDTO chatChannelInitialization)
            throws IsSameUserException, UserNotFoundException {
        String channelUuid = chatService.establishChatSession(chatChannelInitialization, LocalDateTime.now());

        EstablishedChatChannelDTO establishedChatChannel = new EstablishedChatChannelDTO(
                channelUuid,
                userService.getUser(chatChannelInitialization.getUserIdOne()).getUsername(),
                userService.getUser(chatChannelInitialization.getUserIdTwo()).getUsername()
        );

        return JSONResponseHelper.createResponse(establishedChatChannel, HttpStatus.OK);
    }

    @GetMapping(value = "/private-chat/channel/{channelUuid}", produces = "application/json")
    public ResponseEntity<String> getExistingChatMessages(@PathVariable("channelUuid") String channelUuid) {
        List<ChatMessageDTO> messages = chatService.getExistingChatMessages(channelUuid);

        return JSONResponseHelper.createResponse(messages, HttpStatus.OK);
    }



}