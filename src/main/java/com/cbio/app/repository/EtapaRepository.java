package com.cbio.app.repository;

import com.cbio.app.entities.EtapaEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EtapaRepository extends MongoRepository<EtapaEntity, String> {

    public List<EtapaEntity> findAllByComandoEtapa(String comandoEtapa);
}
