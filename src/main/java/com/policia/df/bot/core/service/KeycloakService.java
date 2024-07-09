package com.policia.df.bot.core.service;

import jakarta.ws.rs.core.Response;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Optional;

public interface KeycloakService {

    Optional<List<UserRepresentation>> pesquisarUsuario(String nome);

    void deletarUsuario(String id);

    public String getToken();

}