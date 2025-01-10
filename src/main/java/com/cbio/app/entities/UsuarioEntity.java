package com.cbio.app.entities;

import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.enuns.PerfilEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document("user")
@Data
@Builder
public class UsuarioEntity implements Serializable {

    @Id
    private String id;

    private Long identificadorUsuario;

    private String email;

    private String name;

    private Long ultimaModificacao;

    private String perfil;

    private CompanyDTO company;

    private String idKeycloak;

    @Getter(AccessLevel.NONE)
    private Boolean active;

    @Getter(AccessLevel.NONE)
    private Integer totalChatsReceived; // Para controlar a distribuição igualitária

    public Integer getTotalChatsReceived() {
        if(totalChatsReceived == null) {
            totalChatsReceived = 0;
        }
        return totalChatsReceived;
    }

    public Boolean getActive() {
        if(active == null) {
            active = false;
        }
        return active;
    }
}
