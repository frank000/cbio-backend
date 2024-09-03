package com.cbio.core.v1.dto;

import com.cbio.app.entities.CompanyEntity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;


@Getter
@Setter
@Builder
public class UsuarioDTO implements Serializable {

    private String id;

    private Long identificadorUsuario;

    private String email;

    private String name;

    private Long ultimaModificacao;

    private String perfil;


    private CompanyEntity company;


    @Getter
    @Setter
    public static class UsuarioFormDTO extends UsuarioDTO {

        private String password;

        UsuarioFormDTO(String id, Long identificadorUsuario, String email, String name, Long ultimaModificacao, String perfil, CompanyEntity company, String password) {
            super(id, identificadorUsuario, email, name, ultimaModificacao, perfil, company);
            this.password = password;
        }
    }
}
