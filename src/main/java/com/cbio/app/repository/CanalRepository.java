package com.cbio.app.repository;

import com.cbio.app.entities.CanalEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CanalRepository extends MongoRepository<CanalEntity, String> {

    Optional<CanalEntity> findCanalByTokenAndClienteAndAtivoTrue(String token, String cliente);

    Optional<CanalEntity> findCanalByNomeAndCliente(String nome, String cliente);

    Optional<CanalEntity> findCanalByNomeAndCompanyIdAndCliente(String nome, String companyId, String cliente);

    Boolean existsByTokenAndNomeAndAtivoTrue(String token, String nome);

    Optional<CanalEntity> findByAtivoIsTrueAndCompanyIdAndNome(String companyId, String nome);
}
