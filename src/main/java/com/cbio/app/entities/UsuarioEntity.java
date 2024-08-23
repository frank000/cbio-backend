package com.cbio.app.entities;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuario")
@Data
public class UsuarioEntity {

    @Id
    private String id;

    private Long idUsuario;

    private String firtName;

    private String lastName;

    private Long ultimaModificacao;

}
