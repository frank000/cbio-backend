package com.cbio.app.repository;

import com.cbio.app.entities.SessaoEntity;
import com.cbio.chat.dto.SessionFiltroDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.util.CloseableIterator;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SessaoCustomRepository  {
    private final MongoTemplate mongoTemplate;


    public Stream<SessaoEntity> buscaListaSessoes(final SessionFiltroDTO dto){
        Query query = new Query();

        if(dto.getAttendantId() != null){
            query.addCriteria(Criteria.where("ultimoAtendente._id").is(dto.getAttendantId()));
        }
        if(dto.getCompanyId() != null){
            query.addCriteria(Criteria.where("ultimoAtendente.company.id").is(dto.getAttendantId()));

        }
        return mongoTemplate.stream(query, SessaoEntity.class);
    }
}

