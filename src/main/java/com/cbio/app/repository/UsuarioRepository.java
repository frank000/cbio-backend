package com.cbio.app.repository;

import com.cbio.app.entities.UsuarioEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<UsuarioEntity, String> {

    UsuarioEntity findByIdentificadorUsuario(Long identificadorUsuario);

    Optional<UsuarioEntity> findByPerfilAndId(String perfil, String id);

    Optional<List<UsuarioEntity>> findAllByPerfil(String perfil);
}
