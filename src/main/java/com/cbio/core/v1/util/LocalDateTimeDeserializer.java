package com.cbio.core.v1.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;


public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // Parseando a string como OffsetDateTime
        String date = p.getText();
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(date);

        // Convertendo para LocalDateTime (ignora o fuso horário)
        LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();
        Date from = Date.from(localDateTime.toInstant(offsetDateTime.getOffset()));

        return localDateTime;
    }
}