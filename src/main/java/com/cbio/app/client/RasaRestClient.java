package com.cbio.app.client;

import com.cbio.core.v1.dto.RasaMessageDTO;
import com.cbio.core.v1.dto.RasaMessageOutDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RasaRestClient {

    private final RestTemplate restTemplate;

    @Value("${assistent.rasa.url}")
    private final String webhookUrl = "http://localhost:8080/webhooks/rest/webhook";

    public List<RasaMessageDTO> sendMessage(RasaMessageOutDTO rasaMessageDTO) {
        // Configurar cabeçalhos para indicar o tipo de conteúdo
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        // Criar a entidade com corpo e cabeçalhos
        HttpEntity<RasaMessageOutDTO> requestEntity = new HttpEntity<>(rasaMessageDTO, headers);

        // Enviar a requisição POST
        ResponseEntity<List<RasaMessageDTO>> responseEntity = restTemplate.exchange(
                webhookUrl + "/webhooks/rest/webhook",
                HttpMethod.POST,
                requestEntity,
                (Class<List<RasaMessageDTO>>) (Class<?>) List.class
        );

        // Verificar se a resposta é OK e retornar o corpo da resposta
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            throw new RuntimeException("Failed to send message, status code: " + responseEntity.getStatusCode());
        }
    }
}
