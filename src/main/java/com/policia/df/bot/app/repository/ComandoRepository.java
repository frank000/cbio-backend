package com.policia.df.bot.app.repository;

import com.policia.df.bot.app.entities.ComandoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComandoRepository extends MongoRepository<ComandoEntity, String> {

    ComandoEntity findByNome(String nome);
}
