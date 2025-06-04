package com.cbio.app.repository;

import com.cbio.app.entities.CanalEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CanalRepository extends MongoRepository<CanalEntity, String> {

    Optional<CanalEntity> findCanalByTokenAndClienteAndAtivoTrue(String token, String cliente);

    Optional<CanalEntity> findCanalByNomeAndCliente(String nome, String cliente);

    Optional<CanalEntity> findCanalByNomeAndCompanyIdAndCliente(String nome, String companyId, String cliente);

    Optional<CanalEntity> findCanalByNomeAndCompanyIdAndIdCanal(String nome, String companyId, String idCanal);

    Boolean existsByTokenAndNomeAndAtivoTrue(String token, String nome);

    Boolean existsByNomeAndCompanyId(String nome, String companyId);

    Optional<CanalEntity> findByAtivoIsTrueAndCompanyIdAndNome(String companyId, String nome);
}
