package com.policia.df.bot.app.repository;

import com.policia.df.bot.app.entities.SessaoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface SessaoRepository extends MongoRepository<SessaoEntity, String> {

    public SessaoEntity findByUsuarioAndAtivo(Long usuarioId, Boolean ativo);

}
