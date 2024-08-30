package com.cbio.core.service;

import com.cbio.core.v1.dto.LoginResultDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface LoginService {
    LoginResultDTO login(String username, String password) throws JsonProcessingException;

    void logout(String refreshToken) ;
}
