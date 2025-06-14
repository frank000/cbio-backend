package com.cbio.core.v1.util;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class JsonToMapConverterUtil {
    public Map<String, Object> convertJsonToMap(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Converte a string JSON para Map<String, Object>
            Map<String, Object> map = objectMapper.readValue(
                    jsonString,
                    new TypeReference<Map<String, Object>>() {}
            );
            return map;
        } catch (Exception e) {
            throw new RuntimeException("Falha ao converter JSON para Map", e);
        }
    }
}
