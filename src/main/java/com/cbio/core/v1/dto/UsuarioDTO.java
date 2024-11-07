package com.cbio.core.v1.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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

    private Boolean active;

    private CompanyDTO company;


    @Getter
    @Setter
    public static class UsuarioFormDTO extends UsuarioDTO {

        private String password;

        UsuarioFormDTO(String id, Boolean active, Long identificadorUsuario, String email, String name, Long ultimaModificacao, String perfil, CompanyDTO company, String password) {
            super(id, identificadorUsuario, email, name, ultimaModificacao, perfil, active, company);
            this.password = password;
        }
    }

    @Getter
    @Setter
    public static class UsuarioSessionFormDTO extends UsuarioDTO {

        private String cpf;
        private String telefone1;
        private String telefone2;
        private String email;

        UsuarioSessionFormDTO(String id, Boolean active, String telefone2, String telefone1, String cpf, Long identificadorUsuario, String email, String name, Long ultimaModificacao, String perfil, CompanyDTO company, String password) {
            super(id, identificadorUsuario, email, name, ultimaModificacao, perfil, active, company);
            this.cpf = cpf;
            this.telefone1 = telefone1;
            this.telefone2 = telefone2;
            this.email = email;
        }
    }
}
