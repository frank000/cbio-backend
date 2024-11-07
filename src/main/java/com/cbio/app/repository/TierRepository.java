package com.cbio.app.repository;

import com.cbio.app.entities.TierEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TierRepository extends MongoRepository<TierEntity  , String> {

}
