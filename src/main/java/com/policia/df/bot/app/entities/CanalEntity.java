package com.policia.df.bot.app.entities;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "canal")
public class CanalEntity {

    @Id
    private String id;

    private String nome;

    private String token;

    private Long idCanal;

    private String primeiroNome;

    private String userName;

    private String apiKey;

}
