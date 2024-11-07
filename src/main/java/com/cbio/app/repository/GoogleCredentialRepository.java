package com.cbio.app.repository;

import com.cbio.app.entities.GoogleCredentialEntity;
import com.cbio.chat.models.DialogoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GoogleCredentialRepository extends MongoRepository<GoogleCredentialEntity, String> {

    Optional<GoogleCredentialEntity> findByUserId(String userId);

}
