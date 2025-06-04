package com.cbio.app.meta.facebook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

public class FacebookToken {


    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class CodeRequest implements Serializable {
        private String code;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class FacebookTokenResponsev implements Serializable {
        private String access_token;
        private String token_type;
        private int expires_in;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ErrorResponse implements Serializable{
        private String error;


    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class CodeResponse {
        private String code;

    }
}
