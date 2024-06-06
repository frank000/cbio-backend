package com.policia.df.bot.core.service;

import jakarta.ws.rs.core.Response;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface KeycloakService {

    List<UserRepresentation> pesquisarUsuario(String nome);


    void deletarUsuario(String id);

}