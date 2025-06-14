package com.cbio.app.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Optional;

public class JwtUtil {

    public static Optional<String> getClaimWithoutVerification(String token, String claimName) throws JsonProcessingException {
        // Divide o token em partes
        String[] chunks = token.split("\\.");

        // Decodifica o payload (parte do meio)
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        // Converte para objeto JSON e extrai a claim
        // Aqui você pode usar uma biblioteca JSON como Jackson
        ObjectMapper mapper = new ObjectMapper();
        JsonNode payloadJson = mapper.readTree(payload);
        if(payloadJson.get(claimName) != null){
            return Optional.of(payloadJson.get(claimName).asText());
        }else{
            return Optional.ofNullable(null);
        }
    }
}