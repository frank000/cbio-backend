package com.policia.df.bot.app.repository;

import com.policia.df.bot.app.entities.CanalEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CanalRepository extends MongoRepository<CanalEntity, String> {

    public CanalEntity findCanalByTokenAndCliente(String token, String cliente);

}
