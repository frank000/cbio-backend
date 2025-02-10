package com.cbio.app.web.controller.v1;

import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.ChatbotForwardService;
import com.cbio.core.service.SessaoService;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/session")
public class SessionController implements SecuredRestController {

    private final SessaoService sessaoService;
    private final ChatbotForwardService chatbotForwardService;

    @PutMapping("disconnect-attendance/{channelId}")
    public ResponseEntity<Void> disconnect(@PathVariable String channelId) {
        sessaoService.disconnectAttendance(
                channelId,
                (x, y, z) -> {
                    try {
                        return chatbotForwardService.notifyUserClosingAttendance(x, y, z);
                    } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                             NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                             XmlParserException | InternalException e) {
                        throw new RuntimeException(e);
                    }
                });
        return ResponseEntity.ok().build();

    }

    @PutMapping("connect-attendance/{channelId}")
    public ResponseEntity<Void> dconnect(@PathVariable String channelId) throws Exception {
        sessaoService.connectAttendance(channelId);
        return ResponseEntity.ok().build();

    }
}
