package com.cbio.app.client;

import com.cbio.core.v1.dto.RasaMessageDTO;
import com.cbio.core.v1.dto.outchatmessages.RasaMessageOutDTO;
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

    private final String webhookUrl = "http://localhost";

    public  RasaMessageDTO[] sendMessage(RasaMessageOutDTO rasaMessageDTO, Integer port) {
        // Configurar cabeçalhos para indicar o tipo de conteúdo
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        // Criar a entidade com corpo e cabeçalhos
        HttpEntity<RasaMessageOutDTO> requestEntity = new HttpEntity<>(rasaMessageDTO, headers);

        // Enviar a requisição POST
        String endPoint = webhookUrl + ":" + port.toString() + "/webhooks/rest/webhook";
        try{
            return  restTemplate.postForObject(endPoint, rasaMessageDTO, RasaMessageDTO[].class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send message, status code.");
        }

    }
}
