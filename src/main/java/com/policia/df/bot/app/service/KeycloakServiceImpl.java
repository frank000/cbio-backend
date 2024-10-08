package com.policia.df.bot.app.service;

import com.policia.df.bot.core.service.KeycloakService;
import jakarta.ws.rs.core.Response;
import lombok.Data;
import okhttp3.OkHttp;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@Slf4j
@Data
public class KeycloakServiceImpl implements KeycloakService {

  @Value("${keycloak.realm}")
  public String realm;

  @Value("${keycloak.auth-server-url}")
  public String serverUrl;

  @Value("${api-key.resource}")
  public String clientId;

  @Value("${api-key.secret}")
  public String clientSecret;

  @Value("${api-key.username}")
  public String userName;

  @Value("${api-key.password}")
  public String password;

  Logger logger = Logger.getLogger(KeycloakServiceImpl.class.getName());

  private final Keycloak keycloak;

  @Override
  public Optional<List<UserRepresentation>> pesquisarUsuario(String nome) {

    List<UserRepresentation> listUsers = keycloak.realm(realm).users().searchByUsername(nome, true);

    if(CollectionUtils.isEmpty(listUsers)) {
      logger.info("Não foram encontrados usuários.");
      return Optional.empty();
    }else{
      return Optional.of(listUsers);

    }
  }

  @Override
  public Optional<List<UserRepresentation>> pesquisarUsuarioPorMatricula(String matricula) {

    List<UserRepresentation> listUsers = keycloak.realm(realm).users().searchByFirstName(matricula, false);

    if(CollectionUtils.isEmpty(listUsers)) {
      logger.info("Não foram encontrados usuários.");
      return Optional.empty();
    }else{
      return Optional.of(listUsers);
    }
  }

  @Override
  public void deletarUsuario(String id) {

    logger.info("Início da deleção de um usuário.");

    try{
      keycloak.realm(realm).users().delete(id);

    } catch (Exception e){
      String msgError = String.format("Não foi possível deletar o usuário de ID %s", id);
      log.error(msgError, e);
      throw new RuntimeException(msgError);
    }
  }

  public String getToken() {

    return keycloak.tokenManager().getAccessToken().getToken();

  }
}