package com.cbio.app.repository;

import com.cbio.app.entities.CompanyConfigEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CompanyConfigRepository extends MongoRepository<CompanyConfigEntity, String> {

    Optional<CompanyConfigEntity> findByCompanyId(String companyId);
}
