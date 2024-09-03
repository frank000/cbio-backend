package com.cbio.app.repository;

import com.cbio.app.entities.PhraseEntity;
import com.cbio.app.entities.UsuarioEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PhraseRepository extends MongoRepository<PhraseEntity, String> {

    List<PhraseEntity> findAllByCompanyId(String companyId);
}
