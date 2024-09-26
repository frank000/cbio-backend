package com.cbio.app.web.controller.v1;

import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.SessaoService;
import com.cbio.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/session")
public class SessionController implements SecuredRestController {

    private final UserService userService;
    private final SessaoService sessaoService;


    @PutMapping("disconnect-attendance/{channelId}")
    public ResponseEntity<Void> disconnect(@PathVariable String channelId) {
        sessaoService.disconnectAttendance(channelId);
        return ResponseEntity.ok().build();

    }

    @PutMapping("connect-attendance/{channelId}")
    public ResponseEntity<Void> dconnect(@PathVariable String channelId) throws Exception {
        sessaoService.connectAttendance(channelId);
        return ResponseEntity.ok().build();

    }
}
