package com.cbio.app.repository;

import com.cbio.app.entities.TicketEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketRepository extends MongoRepository<TicketEntity, String> {

}
