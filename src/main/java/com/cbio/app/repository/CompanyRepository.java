package com.cbio.app.repository;

import com.cbio.app.entities.ComandoEntity;
import com.cbio.app.entities.CompanyEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CompanyRepository extends MongoRepository<CompanyEntity, String> {

}
