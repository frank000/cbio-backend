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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="keycloakClient", url = "http://localhost:8080/realms", configuration = KeycloakClient.Configuration.class)
public interface KeycloakClient {

    @PostMapping(value = "rocketchat/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    LoginResultDTO login(@RequestBody CredentialsDTO credentialsDTO);

    @PostMapping(value = "rocketchat/protocol/openid-connect/logout", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    Void logout(@RequestBody CredentialsDTO credentialsDTO);

    class Configuration {
        @Bean
        Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> converters) {
            return new SpringFormEncoder(new SpringEncoder(converters));
        }
    }
}
