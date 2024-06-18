package com.policia.df.bot.app.repository;

import com.policia.df.bot.app.entities.ComandoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ComandoRepository extends MongoRepository<ComandoEntity, String> {

    ComandoEntity findByNome(String nome);

    List<ComandoEntity> findAllByAtivo(Boolean ativo);
}
