package com.policia.df.bot.app.entities;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sessao")
@Data
public class SessaoEntity {
    
    @Id
    private String id;

    private Long sessaoId;

    private CanalEntity canal;

    private UsuarioEntity usuario;

    private Long inicioSessao;

    private Long finalSessao;

    private Long expiresAt;
    
}
