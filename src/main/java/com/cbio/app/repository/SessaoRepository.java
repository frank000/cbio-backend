package com.cbio.app.repository;

import com.cbio.app.entities.SessaoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SessaoRepository extends MongoRepository<SessaoEntity, String> {

    Optional<SessaoEntity> findByAtivoAndIdentificadorUsuarioAndCanalNome(Boolean ativo, Long IdentificadorUsuario, String canal);


    Optional<SessaoEntity> findByAtivoAndIdentificadorUsuarioAndCanalNomeAndChannelUuid(Boolean ativo, Long IdentificadorUsuario, String canal, String channelUuid);

    Optional<SessaoEntity> findByCanalAndIdentificadorUsuario(String canal, Long identificadorUsuario);



}

