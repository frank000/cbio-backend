package com.cbio.app.repository;

import com.cbio.app.entities.CompanyEntity;
import com.cbio.app.entities.TierEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CompanyRepository extends MongoRepository<CompanyEntity, String> {


    Optional<TierEntity> getTierById(String companyId);

    Optional<CompanyEntity> findFirstByOrderByDataCadastroAsc();
}
