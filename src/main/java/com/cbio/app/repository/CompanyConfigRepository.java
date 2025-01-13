package com.cbio.app.repository;

import com.cbio.app.entities.CompanyConfigEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CompanyConfigRepository extends MongoRepository<CompanyConfigEntity, String> {

    Optional<CompanyConfigEntity> findByCompanyId(String companyId);

    @Query(value = "{'companyId': ?0}", fields = "{id : 1 , rag: 1,  companyId : 1, keepSameAttendant:  1, autoSend:  1, model:  1}")
    Optional<CompanyConfigEntity> getPreferencesByCompany(String companyId);


    void deleteByCompanyId(String companyId);
}
