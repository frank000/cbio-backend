package com.cbio.app.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import feign.Headers;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.Map;

@FeignClient(name = "oauthClient", url = "https://api.instagram.com" )
public interface Oauth2Client {

    @PostMapping(value = "oauth/access_token")
    @Headers("Content-Type: multipart/form-data")
    TokenResponseDTO getAccessToken(
            @RequestBody Map<String, ?> form
    );

    @PostMapping(value = "oauth/refresh", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenResponseDTO refreshToken(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("refresh_token") String refreshToken
    );

    @PostMapping(value = "oauth/revoke", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    void revokeToken(
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("token") String token
    );

    // Response DTO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class TokenResponseDTO implements Serializable {

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String token_type;

        private String refreshToken;

        @JsonProperty("expires_in")
        private Long expiresIn;

        private String scope;
    }

//
//    // Custom error decoder for OAuth2 errors
//    class OAuth2ErrorDecoder implements ErrorDecoder {
//        private final ErrorDecoder defaultDecoder = new Default();
//
//        @Override
//        public Exception decode(String methodKey, Response response) {
//            if (response.status() == 400) {
//                return new OAuth2AuthenticationException("Invalid OAuth2 request");
//            }
//            if (response.status() == 401) {
//                return new OAuth2AuthenticationException("Invalid or expired token");
//            }
//            return defaultDecoder.decode(methodKey, response);
//        }
//    }
}