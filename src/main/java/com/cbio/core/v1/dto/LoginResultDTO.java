package com.cbio.core.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LoginResultDTO implements Serializable {

    @JsonProperty("access_token")
    private String access_token;

    @JsonProperty("expires_in")
    private Integer expires_in;

    @JsonProperty("refresh_expires_in")
    private Integer refresh_expires_in;

    @JsonProperty("refresh_token")
    private String refresh_token;

    @JsonProperty("token_type")
    private String token_type;

    @JsonProperty("not-before-policy")
    private Integer notBeforePolicy;

    @JsonProperty("session_state")
    private String session_state;

    @JsonProperty("scope")
    private String scope;
}