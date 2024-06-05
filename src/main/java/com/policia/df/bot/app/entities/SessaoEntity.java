package com.policia.df.bot.app.entities;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sessao")
@Data
public class SessaoEntity {
    
    @Id
    private String id;

    private String sessaoId;

    private Long canal;

    private Long usuario;

    private Long inicioSessao;

    private Long finalSessao;

    private Long expiresAt;

    private Boolean ativo;
    
}
