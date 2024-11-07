package com.cbio.app.service;

import com.cbio.app.client.KeycloakClient;
import com.cbio.app.exception.CbioException;
import com.cbio.core.service.LoginService;
import com.cbio.core.v1.dto.CredentialsDTO;
import com.cbio.core.v1.dto.LoginResultDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    public LoginResultDTO login(String username, String password) throws JsonProcessingException, CbioException {

        try {
            CredentialsDTO credentialsDTO = CredentialsDTO.builder()
                    .client_secret(clientSecret)
                    .username(username)
                    .password(password)
                    .grant_type("password")
                    .client_id(clientId)
                    .build();

            return keycloakClient.login(credentialsDTO);
        }catch (FeignException.Unauthorized unauthorized){
            throw new CbioException("Acesso não autorizado. Favor verifique sua senha e seu e-mail", HttpStatus.UNAUTHORIZED.value());
        }catch (Exception e){
            throw e;
        }
    }

    public void logout(String refreshToken) {

        keycloakClient.logout(clientId,  refreshToken);
    }
}
