package com.cbio.app.web.controller.v1;

import com.cbio.app.web.SecuredRestController;
import com.cbio.chat.dto.ChatDTO;
import com.cbio.core.service.DialogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/dialog")
public class DialogController implements SecuredRestController {

    private final DialogoService dialogoService;


    @GetMapping("/sender/session/{sessionId}/channel/{channelId}")
    public ResponseEntity<  List<ChatDTO>> obtemGrid(@PathVariable String sessionId, @PathVariable String channelId) {
        List<ChatDTO> collect = dialogoService.mountChatFromDioalogBySessionIdAndChannelId(sessionId, channelId);


        return ResponseEntity.ok(collect);
    }


}
