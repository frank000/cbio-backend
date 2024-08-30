package com.cbio.app.entities;

import com.cbio.core.v1.enuns.PerfilEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
@Data
@Builder
public class UsuarioEntity {

    @Id
    private String id;

    private Long identificadorUsuario;

    private String email;

    private String name;

    private Long ultimaModificacao;

    private String perfil;

    @DBRef
    private CompanyEntity company;

    private String idKeycloak;
}
