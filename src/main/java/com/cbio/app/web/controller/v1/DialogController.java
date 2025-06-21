package com.cbio.app.web.controller.v1;

import com.cbio.app.web.SecuredRestController;
import com.cbio.chat.dto.ChatDTO;
import com.cbio.core.service.DialogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/dialog")
public class DialogController implements SecuredRestController {

    private final DialogoService dialogoService;


    @GetMapping("/sender/session/{sessionId}/channel/{channelId}")
    public ResponseEntity<Page<ChatDTO>> obtemGrid(
            @PathVariable String sessionId,
            @PathVariable String channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "data,asc") String sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("data")));
        Page<ChatDTO> messagesPage = dialogoService.getPaginatedMessages(sessionId, channelId, pageable);

        return ResponseEntity.ok(messagesPage);
    }

}
