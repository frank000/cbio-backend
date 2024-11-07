package com.cbio.app.repository;

import com.cbio.app.entities.SessaoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SessaoRepository extends MongoRepository<SessaoEntity, String> {

    Optional<SessaoEntity> findByAtivoAndIdentificadorUsuarioAndCanalNomeAndCanalIdCanal(Boolean ativo, Long IdentificadorUsuario, String canal, String idCanal);

    Optional<SessaoEntity> findByAtivoAndIdentificadorUsuarioAndCanalIdCanal(Boolean ativo, Long IdentificadorUsuario, String idCanal);


    Optional<SessaoEntity> findByAtivoAndIdentificadorUsuarioAndCanalNomeAndLastChannelChatChannelUuid(Boolean ativo, Long IdentificadorUsuario, String canal, String channelUuid);

    Optional<SessaoEntity> findByCanalAndIdentificadorUsuario(String canal, Long identificadorUsuario);

    Optional<SessaoEntity> findByLastChannelChatChannelUuid(String channelUuid);




}

