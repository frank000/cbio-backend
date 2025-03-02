package com.cbio.app.repository;

import com.cbio.app.entities.SessaoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SessaoRepository extends MongoRepository<SessaoEntity, String> {

    Optional<SessaoEntity> findByAtivoAndIdentificadorUsuarioAndCanalNomeAndCanalIdCanal(Boolean ativo, Long IdentificadorUsuario, String canal, String idCanal);

//    Optional<SessaoEntity> findByAtivoAndIdentificadorUsuarioAndCanalIdCanal(Boolean ativo, Long IdentificadorUsuario, String idCanal);

    @Query(value = "{ 'ativo': ?0, 'identificadorUsuario': ?1, 'canal.idCanal': ?2 }")
    Optional<SessaoEntity> findByAtivoAndIdentificadorUsuarioAndCanalIdCanal(
            @Param("ativo") Boolean ativo,
            @Param("identificadorUsuario") Long identificadorUsuario,
            @Param("idCanal") String idCanal
    );

    Optional<SessaoEntity> findByAtivoAndIdentificadorUsuarioAndCanalNomeAndLastChannelChatChannelUuid(Boolean ativo, Long IdentificadorUsuario, String canal, String channelUuid);

    Optional<SessaoEntity> findByCanalAndIdentificadorUsuario(String canal, Long identificadorUsuario);

    Optional<SessaoEntity> findByLastChannelChatChannelUuid(String channelUuid);




}

