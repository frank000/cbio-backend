package com.cbio.app.service;

import com.cbio.app.configuration.KeyCloakConfig;
import com.cbio.app.configuration.keycloak.Credentials;
import com.cbio.core.service.IAMService;
import com.cbio.core.v1.dto.UserKeycloak;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
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

    public List<UserRepresentation> getUser(String userName) {
        UsersResource usersResource = getInstance();

        List<UserRepresentation> user = usersResource.searchByUsername(userName, true);
        return user;
    }

    @Override
    public List<UserRepresentation> getUser(UserKeycloak userVO) {
        return List.of();
    }

    private String getUserId(UserKeycloak userVO) {
        String id = "";
        List<UserRepresentation> users = getUser(userVO.getUserName());
        for (UserRepresentation ur : users) {
            id = ur.getId();
        }
        return id;
    }

    public String addUser(UserKeycloak userVO, String roleUser) {
        logger.info("Iniciando Cadastro do usuário no keycloak: ");

        verifyIfUserExists(userVO, true);

        logger.info("Criando usuário de keycloak: " + userVO.getFirstname() + " - " + userVO.getUserName());

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("sid", List.of(userVO.getIdUser()));
        attributes.put("companyId", List.of(userVO.getIdCompany()));
        attributes.put("tempPassword", List.of("false"));

        CredentialRepresentation credential = Credentials
                .createPasswordCredentials(userVO.getPassword());
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

    public void updateUserPassword(UserKeycloak userVO, String userID) {

        CredentialRepresentation credential = Credentials.createPasswordCredentials(userVO.getPassword());

        UsersResource usersResource = getInstance();

        // Busca o usuário no Keycloak usando o nome de usuário antigo (ou outro critério)
        List<UserRepresentation> user1 = getUser(userVO.getOldUserName());
        UserRepresentation userKeycloak = user1.stream()
                .filter(item -> item.getId().equals(userVO.getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Usuário Keycloak não encontrado"));

        userKeycloak.setCredentials(Collections.singletonList(credential));

        usersResource.get(userID).update(userKeycloak);
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
        List<UserRepresentation> user1 = getUser(userVO.getOldUserName());
        UserRepresentation userKeyclaok = user1.stream()
                .filter(item -> item.getId().equals(userVO.getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User Keyclaok não encontrado"));

        userKeyclaok.setEmail(userVO.getEmail());
//        userKeyclaok.setUsername(userVO.getUserName()); //READ-ONLY
        userKeyclaok.setCredentials(Collections.singletonList(credential));

        usersResource.get(userID).update(userKeyclaok);
    }

    public void deleteUser(UserKeycloak userVO) {
        logger.info("Deletando usuário no Keycloak: " + userVO.getFirstname() + " - " + userVO.getUserName());
        getKeycloak().realm(realm).users().delete(getUserId(userVO));
    }
    public void deleteUserByUserName(String username) {
        logger.info(String.format("Deletando usuário no Keycloak: %s", username));

        UserKeycloak userKeycloak = UserKeycloak.builder()
                        .userName(username)
                                .build();
        getKeycloak().realm(realm).users().delete(getUserId(userKeycloak));
    }

    public void updateUserAttributes(UserKeycloak userVO, Map<String, List<String>> attributes) {
        var users = getUser(userVO.getUserName());
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
            throw new BadRequestException("Usuário já existe. Tente outro email ou contate um administrador.");
//            logger.info("Usuário já existe no Keycloak: " + userVO.getFirstname() + " - " + userVO.getUserName());
//            deleteUser(userVO);
        }
    }

    public void excluirCidadaoService(String id) {
        KeyCloakConfig keyCloakConfig = new KeyCloakConfig();
        Keycloak keycloak = keyCloakConfig.getInstanceCidadaoService(serverUrlCidadao, realm, userNameCidadao, passwordCidadao, clientIdCidadao, clientSecretCidadao);
        keycloak.realm(realm).users().delete(id);
    }

    private UsersResource getInstanceCidadaoService() {
        KeyCloakConfig keyCloakConfig = new KeyCloakConfig();
        return keyCloakConfig
                .getInstanceCidadaoService(serverUrlCidadao, realm, userNameCidadao, passwordCidadao, clientIdCidadao, clientSecretCidadao)
                .realm(realm)
                .users();
    }
}
