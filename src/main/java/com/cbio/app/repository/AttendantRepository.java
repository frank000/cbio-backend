package com.cbio.app.repository;

import com.cbio.app.entities.AttendantEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AttendantRepository extends MongoRepository<AttendantEntity, String> {



}
