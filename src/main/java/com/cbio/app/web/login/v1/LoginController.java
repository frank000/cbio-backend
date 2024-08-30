package com.cbio.app.web.login.v1;

import com.cbio.core.service.LoginService;
import com.cbio.core.v1.dto.CredentialsDTO;
import com.cbio.core.v1.dto.LoginDTO;
import com.cbio.core.v1.dto.LoginResultDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/v1/login")
@RestController
public class LoginController {


    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<LoginResultDTO> login(@RequestBody LoginDTO loginDTO) throws JsonProcessingException {

        LoginResultDTO login = loginService.login(loginDTO.getEmail(), loginDTO.getPassword());

        return ResponseEntity.ok().body(login);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody String refreshToken) {

        loginService.logout(refreshToken);

        return ResponseEntity.ok().build();
    }
}
