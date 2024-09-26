package com.cbio.chat.controllers;

import com.cbio.chat.dto.WebsocketNotificationDTO;
import com.cbio.core.service.SessaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/chat-session")
@RequiredArgsConstructor
public class ChatSessionController {



    private final SessaoService sessaoService;

    @GetMapping
    public ResponseEntity<List<WebsocketNotificationDTO>> getChatSessions() {
        return ResponseEntity.ok(sessaoService.getChatSessions());
    }

}