package com.cbio.app.client;

import com.cbio.core.v1.dto.SelecaoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RequiredArgsConstructor
@Service
public class IbgeRestClient {

    private final RestTemplate restTemplate;

    @Value("${ibge.url}")
    private String webhookUrl;

    public List<SelecaoDTO> getCidades(Integer idUf) {
        // Configurar cabeçalhos para indicar o tipo de conteúdo
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Criar a entidade com corpo e cabeçalhos
        HttpEntity<Object> requestEntity = new HttpEntity<>(idUf, headers);

        // Enviar a requisição POST
        ResponseEntity<List<SelecaoDTO>> responseEntity = restTemplate.exchange(
                webhookUrl + String.format("/api/v1/localidades/estados/%s/municipios", idUf),
                HttpMethod.GET,
                requestEntity,
                (Class<List<SelecaoDTO>>) (Class<?>) List.class
        );

        // Verificar se a resposta é OK e retornar o corpo da resposta
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            throw new RuntimeException("Failed to send message, status code: " + responseEntity.getStatusCode());
        }
    }
}
