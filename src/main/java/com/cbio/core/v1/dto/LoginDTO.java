package com.cbio.core.v1.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LoginDTO implements Serializable {

    private String email;
    private String password;
}
