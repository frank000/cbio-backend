package com.policia.df.bot.app.entities;

import com.policia.df.bot.app.repository.SessaoRepository;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "sessao")
@Getter
@Setter
@Builder
public class SessaoEntity {
    
    @Id
    private String id;

    private UUID sessaoId;

    private Long canal;

    @Indexed
    private Long usuario;

    private Long inicioSessao;

    private Long finalSessao;

    private Long expiresAt;

    private Boolean ativo;

    private String ultimaEtapa;

    private String comandoExecucao;

    @DBRef()
    private AtendenteEntity ulitmoAtendente;

    private List<AtendimentoDTO> atendimentoDTOS;

    public void flush(SessaoRepository sessaoRepository){
        this.setComandoExecucao("");
        this.setUltimaEtapa("");
        sessaoRepository.save(this);
    }


    public class AtendimentoDTO{

        private String atendenteNome;

        private LocalDateTime dataHora;
    }
}
