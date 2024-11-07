package com.cbio.app.repository;

import com.cbio.app.entities.EventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<EventEntity, String> {

}
