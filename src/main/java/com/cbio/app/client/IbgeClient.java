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

@FeignClient(name="ibgeClient", url = "https://servicodados.ibge.gov.br")
public interface IbgeClient {

    @PostMapping(value = "rocketchat/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    LoginResultDTO login(@RequestBody CredentialsDTO credentialsDTO);

    @PostMapping(value = "rocketchat/protocol/openid-connect/logout", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    Void logout(@RequestBody CredentialsDTO credentialsDTO);

}
