package com.cbio.app.service;

import com.cbio.app.client.Oauth2Client;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class InstagramOauthService {
    private final RestTemplate restTemplate;

    /**
     * Faz uma requisição GET ao endpoint /me da Instagram Graph API.
     *
     * @param accessToken O token de acesso.
     * @return A resposta JSON contendo o id e username do usuário.
     */
    public UserInfoMeta getUserInfo(String accessToken) {

        String url = "https://graph.instagram.com/v21.0/me?fields=id,name,user_id&access_token=" + accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UserInfoMeta> entity = new HttpEntity<>(null, headers);

     ResponseEntity<UserInfoMeta> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                UserInfoMeta.class
        );

        return response.getBody();
    }

    /**
     * Inscreve a aplicação nos eventos de comentários e mensagens.
     *
     * @param accessToken O token de acesso do Instagram.
     * @param pageId O ID da página para a qual você está inscrevendo.
     * @return A resposta da API do Instagram.
     */
    public Map<String, Object> subscribeToFields(String accessToken, String pageId) {

        // URL de endpoint para inscrição
        String url = String.format(
                "https://graph.instagram.com/v21.0/%s/subscribed_apps?subscribed_fields=comments%%2Cmessages&access_token=%s",
                pageId, accessToken);

        // Criar a solicitação POST
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity< Map<String, Object>> entity = new HttpEntity<>(null, headers);

        // Enviar a solicitação POST
        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,  // Método POST
                entity,           // Corpo da solicitação (não necessário para este caso específico)
                Map.class      // Espera-se que a resposta seja um JSON String
        );

        // Retornar o corpo da resposta
        return response.getBody();
    }

    /**
     * Short Lived Token
     * @param clientId
     * @param clientSecret
     * @param code
     * @param redirectUri
     * @return
     */
    public Oauth2Client.TokenResponseDTO getAccessToken(String clientId, String clientSecret, String code, String redirectUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("grant_type", "authorization_code");
        map.add("code", code);
        map.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        return restTemplate.postForObject(
                "https://api.instagram.com/oauth/access_token",
                request,
                Oauth2Client.TokenResponseDTO.class
        );
    }

    /**
     * Long Lived Token
     * @param clientSecret
     * @param shortLivedAccessToken
     * @return
     */
    public Oauth2Client.TokenResponseDTO exchangeTokenForLongLived(String clientSecret, String shortLivedAccessToken) {

        String url = "https://graph.instagram.com/access_token" +
                "?grant_type=ig_exchange_token" +
                "&client_secret=" + clientSecret +
                "&access_token=" + shortLivedAccessToken;

        ResponseEntity<Oauth2Client.TokenResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                Oauth2Client.TokenResponseDTO.class
        );

        return response.getBody();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfoMeta implements Serializable {
        String username;
        @JsonProperty("user_id")
        String userId;
        String id;
    }
}