package com.cbio.core.v1.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserKeycloak {

    private String id;

    private String idUser;

    private String idCompany;

    private String userName;

    private String oldUserName;

    private String email;

    private String password;

    private String firstname;

    private String lastName;

    private Boolean temporaryPassword;

}