package com.cbio.app.repository;

import com.cbio.app.entities.InstagramCredentialEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InstagramCredentialRepository extends MongoRepository<InstagramCredentialEntity, String> {

    Optional<InstagramCredentialEntity> findByCompanyIdAndExpirateTimeBefore(String companyId, LocalDateTime expirationTime);
    Optional<InstagramCredentialEntity> findByCompanyId(String companyId);
}
