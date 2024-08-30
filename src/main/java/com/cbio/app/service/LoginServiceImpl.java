package com.cbio.app.service;

import com.cbio.app.client.KeycloakClient;
import com.cbio.core.service.LoginService;
import com.cbio.core.v1.dto.CredentialsDTO;
import com.cbio.core.v1.dto.LoginResultDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

@RequiredArgsConstructor
@Service
@CrossOrigin("*")
public class LoginServiceImpl implements LoginService {

    private final KeycloakClient keycloakClient;

    @Value("${login.keycloak.client-secret}")
    private String clientSecret;

    @Value("${login.keycloak.client-id}")
    private String clientId;

    @Override
    public LoginResultDTO login(String username, String password) throws JsonProcessingException {

        CredentialsDTO credentialsDTO = CredentialsDTO.builder()
                .client_secret(clientSecret)
                .username(username)
                .password(password)
                .grant_type("password")
                .client_id(clientId)
                .build();

        return keycloakClient.login(credentialsDTO);
    }

    public void logout(String refreshToken) {

        CredentialsDTO dto = CredentialsDTO.builder()
                .client_secret(clientSecret)
                .refresh_token(refreshToken)
                .grant_type("password")
                .client_id(clientId)
                .build();

        keycloakClient.logout(dto);
    }
}
