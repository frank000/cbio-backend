package com.cbio.app.repository;

import com.cbio.app.entities.ComandoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ComandoRepository extends MongoRepository<ComandoEntity, String> {

    ComandoEntity findByNome(String nome);

    List<ComandoEntity> findAllByAtivo(Boolean ativo);
}
