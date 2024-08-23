package com.cbio.app.repository;

import com.cbio.app.entities.UsuarioEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<UsuarioEntity, String> {

    public UsuarioEntity findByIdUsuario(Long idUsuario);

}
