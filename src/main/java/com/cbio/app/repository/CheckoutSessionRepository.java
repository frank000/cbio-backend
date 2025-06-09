package com.cbio.app.repository;

import com.cbio.app.entities.CheckoutSessionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CheckoutSessionRepository extends MongoRepository<CheckoutSessionEntity, String> {


    Optional<CheckoutSessionEntity> findBySessionId(String sessionId);
    Optional<CheckoutSessionEntity> findBySubscriptionId(String subscriptionId);
    List<CheckoutSessionEntity> findAllByCompanyId(String copanyId);
}
