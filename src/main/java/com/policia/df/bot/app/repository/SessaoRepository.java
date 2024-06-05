package com.policia.df.bot.app.repository;

import com.policia.df.bot.app.entities.SessaoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SessaoRepository extends MongoRepository<SessaoEntity, String> {

    public SessaoEntity findBySessaoIdAndAtivo(String sessaoId, Boolean ativo);

}
