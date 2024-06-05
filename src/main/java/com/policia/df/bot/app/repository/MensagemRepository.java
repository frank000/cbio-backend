package com.policia.df.bot.app.repository;

import com.policia.df.bot.app.entities.MensagemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MensagemRepository extends MongoRepository<MensagemEntity, String> {

}
