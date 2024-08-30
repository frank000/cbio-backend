package com.cbio.core.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class CredentialsDTO implements Serializable {

    private String username;

    private String password;

    @JsonProperty("client_secret")
    private String client_secret;

    @JsonProperty("grant_type")
    private String grant_type;

    @JsonProperty("client_id")
    private String client_id;

    private String refresh_token;
}
