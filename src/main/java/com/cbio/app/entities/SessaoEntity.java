package com.cbio.app.entities;

import com.cbio.app.repository.SessaoRepository;
import com.cbio.core.v1.dto.AttendantDTO;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
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

    private String canal;

    @Indexed
    private Long identificadorUsuario;

    private Long inicioSessao;

    private Long finalSessao;

    private Long expiresAt;

    private Boolean ativo;

    private Boolean atendimentoAberto;

    private String ultimaEtapa;

    private String comandoExecucao;

    private AttendantDTO ulitmoAtendente;

    private List<AtendimentoDTO> atendimentoDTOS;

    public void flush(SessaoRepository sessaoRepository){
        this.setComandoExecucao("");
        this.setUltimaEtapa("");
        sessaoRepository.save(this);
    }

    @Getter
    @Setter
    @Builder
    public static class AtendimentoDTO implements Serializable {

        private String atendenteNome;

        private LocalDateTime dataHora;
    }
}
