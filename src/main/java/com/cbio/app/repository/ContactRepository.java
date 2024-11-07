package com.cbio.app.repository;

import com.cbio.app.entities.ContactEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ContactRepository extends MongoRepository<ContactEntity, String> {

    List<ContactEntity> findByCompanyId(String companyId);
}
