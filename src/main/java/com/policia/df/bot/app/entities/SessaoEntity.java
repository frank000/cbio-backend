package com.policia.df.bot.app.entities;

import com.policia.df.bot.app.repository.SessaoRepository;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "sessao")
@Data
public class SessaoEntity {
    
    @Id
    private String id;

    private UUID sessaoId;

    private Long canal;

    private Long usuario;

    private Long inicioSessao;

    private Long finalSessao;

    private Long expiresAt;

    private Boolean ativo;

    private String ultimaAcao;

    private String ultimoComando;

    public void flush(SessaoRepository sessaoRepository){
        this.setUltimoComando("");
        this.setUltimaAcao("");
        sessaoRepository.save(this);
    }

}
