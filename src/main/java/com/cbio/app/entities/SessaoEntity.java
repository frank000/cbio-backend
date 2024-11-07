package com.cbio.app.entities;

import com.cbio.app.repository.SessaoRepository;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.ContactDTO;
import com.cbio.core.v1.dto.UsuarioDTO;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private String nome;

    private String cpf;

    private String telefone1;

    private String telefone2;

    private String email;

    private CanalDTO canal;

    @Indexed
    private Long identificadorUsuario;

    private Long inicioSessao;

    private Long finalSessao;

    private Long expiresAt;

    private Boolean ativo;

    private Boolean atendimentoAberto;

    private LocalDateTime dataHoraAtendimentoAberto;

    private String ultimaEtapa;

    private ContactDTO contact;

    @Setter(AccessLevel.NONE)
    private ChannelChatDTO lastChannelChat;

    @Getter(AccessLevel.NONE)
    private List<ChannelChatDTO> historyLastChannelChat;

    private String comandoExecucao;

    private UsuarioDTO ultimoAtendente;



    private List<AtendimentoDTO> atendimentoDTOS;

    public void setLastChannelChat(ChannelChatDTO lastChannelChat){
        this.lastChannelChat = lastChannelChat;
        this.getHistoryLastChannelChat().add(lastChannelChat);
    }

    public List<ChannelChatDTO> getHistoryLastChannelChat(){
        if(this.historyLastChannelChat == null){
            this.historyLastChannelChat = new ArrayList<>();
        }
        return this.historyLastChannelChat;
    }

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

    @Getter
    @Setter
    @Builder
    public static class ChannelChatDTO implements Serializable {

        private String channelUuid;

        private LocalDateTime dateTimeStart;
    }


}
