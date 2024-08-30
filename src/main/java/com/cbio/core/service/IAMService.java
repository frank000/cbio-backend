package com.cbio.core.service;

import com.cbio.core.v1.dto.UserKeycloak;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Map;

public interface IAMService {
    List<UserRepresentation> getUser(UserKeycloak userVO);

    String addUser(UserKeycloak userVO,  String roleUser);

    void updateUser(UserKeycloak userVO, String userID);

    void deleteUser(UserKeycloak userVO);

    void updateUserAttributes(UserKeycloak userVO, Map<String, List<String>> attributes);
}
