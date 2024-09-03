package com.cbio.chat.controllers;

import com.cbio.chat.dto.WebsocketNotificationDTO;
import com.cbio.chat.services.ChatService;
import com.cbio.chat.services.UserChatService;
import com.cbio.core.service.SessaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/chat-session")
@RequiredArgsConstructor
public class ChatSessionController {

    private final ChatService chatService;

    private final UserChatService userService;

    private final SessaoService sessaoService;

    @GetMapping
    public ResponseEntity<List<WebsocketNotificationDTO>> getChatSessions() {
        return ResponseEntity.ok(sessaoService.getChatSessions());
    }

}