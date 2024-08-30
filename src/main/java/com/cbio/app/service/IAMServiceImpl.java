package com.cbio.app.service;

import com.cbio.app.configuration.KeyCloakConfig;
import com.cbio.app.configuration.keycloak.Credentials;
import com.cbio.core.service.IAMService;
import com.cbio.core.v1.dto.UserKeycloak;
import jakarta.ws.rs.core.Response;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
@Data
@RequiredArgsConstructor
public class IAMServiceImpl implements IAMService {


    private Logger logger = Logger.getLogger(IAMService.class.getName());

    @Value("${api-key.rocketchat.realm}")
    public String realm;


    @Value("${api-key.password}")
    public String password;

    @Value("${api-key.rocketchat.auth-server-url}")
    private String serverUrlCidadao;

    @Value("${api-key.rocketchat.realm}")
    private String realmCidadao;

    @Value("${api-key.rocketchat.resource}")
    private String clientIdCidadao;

    @Value("${api-key.rocketchat.secret}")
    private String clientSecretCidadao;

    @Value("${api-key.rocketchat.username}")
    private String userNameCidadao;

    @Value("${api-key.rocketchat.password}")
    private String passwordCidadao;

    private final Keycloak keycloak;

    public final static String ROLE_USER = "user";
    public final static String ROLE_ADMIN = "admin";
    public final static String ROLE_ATTENDANT = "attendant";

    public List<UserRepresentation> getUser(UserKeycloak userVO) {
        UsersResource usersResource = getInstance();
        List<UserRepresentation> user = usersResource.searchByUsername(userVO.getUserName(), true);
        return user;

    }

    private String getUserId(UserKeycloak userVO) {
        String id = "";
        List<UserRepresentation> users = getUser(userVO);
        for (UserRepresentation ur : users) {
            id = ur.getId();
        }
        return id;
    }

    public String addUser(UserKeycloak userVO,  String roleUser) {
        logger.info("Iniciando Cadastro do usuário no keycloak: ");

        verifyIfUserExists(userVO, true);

        logger.info("Criando usuário de keycloak: " + userVO.getFirstname() + " - " + userVO.getUserName());

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("sid", List.of(userVO.getIdUser()));
        attributes.put("tempPassword", List.of("false"));

        CredentialRepresentation credential = Credentials
                .createPasswordCredentials(userVO.getPassword());
        System.out.println("senha > " + userVO.getPassword());
//        credential.setType("password");
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userVO.getUserName());
        user.setFirstName(userVO.getFirstname());
//        user.setLastName(userVO.getLastName());
        user.setEmail(userVO.getEmail());
        user.setCredentials(Collections.singletonList(credential));
        user.setAttributes(attributes);
        user.setEnabled(true);

        UsersResource instance = getInstance();
        Response response = instance.create(user);
        System.out.println("Response |  Status: " + response.getStatus() + " | Status Info: " + response.getStatusInfo());
        //String userIds = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        //System.out.println("User Id Registred : {" + userIds+ "} " );


        String userId = CreatedResponseUtil.getCreatedId(response);

        assignRoles(userId, roleUser);
        return userId;
    }

    public void updateUser(UserKeycloak userVO, String userID) {
        CredentialRepresentation credential = Credentials
                .createPasswordCredentials(userVO.getPassword());
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userVO.getUserName());
        user.setFirstName(userVO.getFirstname());
        user.setLastName(userVO.getLastName());
        user.setEmail(userVO.getEmail());
        user.setCredentials(Collections.singletonList(credential));

        UsersResource usersResource = getInstance();
//	    if(StringUtils.isBlank(userID)) {
//	    	userID = getUserId(userVO);
//
//	    }
        //sempre buscar o id do keycloack
        userID = getUserId(userVO);
        usersResource.get(userID).update(user);
    }

    public void deleteUser(UserKeycloak userVO) {
        logger.info("Deletando usuário no Keycloak: " + userVO.getFirstname() + " - " + userVO.getUserName());
        getKeycloak().realm(realm).users().delete(getUserId(userVO));
    }

    public void updateUserAttributes(UserKeycloak userVO, Map<String, List<String>> attributes) {
        var users = getUser(userVO);
        if (users != null) {
            users.forEach(user -> {
                var userAttr = user.getAttributes();
                if (userAttr == null) {
                    userAttr = attributes;
                } else {
                    userAttr.putAll(attributes);
                }
                user.setAttributes(userAttr);
                var userResource = getKeycloak().realm(realm).users().get(user.getId());
                userResource.update(user);
            });
        }
    }

    public UsersResource getInstance() {
        return keycloak.realm(realm).users();
    }

    public Keycloak getKeycloak() {
        return keycloak;
    }

    private void assignRoles(String userId, String role) {
        RoleRepresentation savedRoleRepresentation = getKeycloak().realm(realm).roles().get(role).toRepresentation();
        List<RoleRepresentation> roleList = new ArrayList<>();
        roleList.add(savedRoleRepresentation);
        getKeycloak().realm(realm)
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(roleList);

    }

    private void verifyIfUserExists(UserKeycloak userVO, boolean deleteIfExists) {
        logger.info("Verificando se o usuário já existe no Keycloak: " + userVO.getFirstname() + " - " + userVO.getUserName());
        String id = getUserId(userVO);
        if (StringUtils.isNotBlank(id) && deleteIfExists) {
            logger.info("Usuário já existe no Keycloak: " + userVO.getFirstname() + " - " + userVO.getUserName());
            deleteUser(userVO);
        }
    }

//    public ExcluirContaApp getCidadao(String cpf) {
//        UsersResource usersResource = getInstanceCidadaoService();
//        List<UserRepresentation> users = usersResource.search(cpf, true);
//        for (UserRepresentation user : users) {
//            ExcluirContaApp conta = ExcluirContaApp.builder()
//                    .idKeycloak(user.getId())
//                    .nomeUsuario(user.getFirstName())
//                    .email(user.getEmail())
//                    .build();
//
//            return conta;
//        }
//        return null;
//    }

    public void excluirCidadaoService(String id) {
        KeyCloakConfig keyCloakConfig = new KeyCloakConfig();
        Keycloak keycloak = keyCloakConfig.getInstanceCidadaoService(serverUrlCidadao, realmCidadao, userNameCidadao, passwordCidadao, clientIdCidadao, clientSecretCidadao);
        keycloak.realm(realmCidadao).users().delete(id);
    }

    private UsersResource getInstanceCidadaoService() {
        KeyCloakConfig keyCloakConfig = new KeyCloakConfig();
        return keyCloakConfig
                .getInstanceCidadaoService(serverUrlCidadao, realmCidadao, userNameCidadao, passwordCidadao, clientIdCidadao, clientSecretCidadao)
                .realm(realmCidadao)
                .users();
    }
}
