package com.cbio.app.repository;

import com.cbio.app.entities.PlanEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlanRepository extends MongoRepository<PlanEntity, String> {

    @Query(value = "{'type': ?0}")
    Optional<PlanEntity> findByType(@Param("type") String type);
}
