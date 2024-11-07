package com.cbio.app.client;

import com.cbio.core.v1.dto.CredentialsDTO;
import com.cbio.core.v1.dto.LoginResultDTO;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name="keycloakClient", url = "http://localhost:8080/realms", configuration = KeycloakClient.Configuration.class)
public interface KeycloakClient {

    @PostMapping(value = "rocketchat/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    LoginResultDTO login(@RequestBody CredentialsDTO credentialsDTO);

    @PostMapping(value = "rocketchat/protocol/openid-connect/logout", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    Void logout(@RequestParam("client_id") String clientId,             // Client ID
                @RequestParam("refresh_token") String refreshToken );

    class Configuration {
        @Bean
        Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> converters) {
            return new SpringFormEncoder(new SpringEncoder(converters));
        }
    }
}
