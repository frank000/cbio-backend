package com.cbio.app.repository;

import com.cbio.app.entities.MensagemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MensagemRepository extends MongoRepository<MensagemEntity, String> {

}
