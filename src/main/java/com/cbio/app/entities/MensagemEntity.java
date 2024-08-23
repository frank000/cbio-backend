package com.cbio.app.entities;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mensagem")
@Data
public class MensagemEntity {

    @Id
    private String id;

    private Long chatId;

    private Long messagemId;

    private Long timestamp;

    private String text;

    private String sessao;

    private Long canalId;

    private Long usuarioId;

}
