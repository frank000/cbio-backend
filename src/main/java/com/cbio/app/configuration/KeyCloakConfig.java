package com.cbio.app.configuration;


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
public class KeyCloakConfig {

    Logger logger = Logger.getLogger(KeyCloakConfig.class.getName());

    @Value("${api-key.rocketchat.auth-server-url}")
    private String serverUrl;

    @Value("${api-key.rocketchat.realm}")
    private String realm;

    @Value("${api-key.resource}")
    private String clientId;

    @Value("${api-key.rocketchat.secret}")
    private String clientSecret;

    @Value("${api-key.rocketchat.username}")
    private String userName;

    @Value("${api-key.rocketchat.password}")
    private String password;

    private String scope = "openid";

    public static Keycloak keycloak = null;

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

    public Keycloak getInstanceCidadaoService(String serverUrl, String realm, String userName,
                                              String password, String clientId, String clientSecret){
        if(keycloak == null){
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(userName)
                    .password(password)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .resteasyClient(new ResteasyClientBuilderImpl()
                            .connectionPoolSize(10)
                            .build())
                    .build();
        }
        return keycloak;
    }
}