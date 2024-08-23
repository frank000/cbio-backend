package com.cbio.app.repository;

import com.cbio.app.entities.SessaoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SessaoRepository extends MongoRepository<SessaoEntity, String> {

    Optional<SessaoEntity> findByAtivoAndIdentificadorUsuarioAndCanal(Boolean ativo, Long IdentificadorUsuario, String canal);

    Optional<SessaoEntity> findByCanalAndIdentificadorUsuario(String canal, Long identificadorUsuario);

}
