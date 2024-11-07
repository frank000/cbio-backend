package com.cbio.app.repository;

import com.cbio.app.entities.ModelEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ModelRepository extends MongoRepository<ModelEntity, String> {

    Optional<ModelEntity> findByName(String name);
}
