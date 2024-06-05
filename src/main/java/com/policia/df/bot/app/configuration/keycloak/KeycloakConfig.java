package com.policia.df.bot.app.configuration.keycloak;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;


@Configuration
@Slf4j
public class KeycloakConfig {

    Logger logger = Logger.getLogger(KeycloakConfig.class.getName());

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${api-key.resource}")
    private String clientId;

    @Value("${api-key.secret}")
    private String clientSecret;

    @Value("${api-key.username}")
    private String userName;

    @Value("${api-key.password}")
    private String password;

    private String scope = "openid";


    @Bean
    public Keycloak keycloak() {

        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .username(userName)
                .password(password)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .scope(scope)
                .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
                .build();
    }
}