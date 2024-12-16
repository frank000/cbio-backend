package com.cbio.app.service;

import com.cbio.app.entities.TicketEntity;
import com.cbio.core.service.ProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class ProtocolServiceImpl implements ProtocolService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Método que gera o número de protocolo
    public String generateProtocolNumber(String year) {

        Query query = new Query();
        query.addCriteria(Criteria.where("protocolNumber").regex("^" + year)); // Busca por protocolos que começam com o ano
        query.with(Sort.by(Sort.Order.desc("protocolNumber")));
        query.limit(1);

        TicketEntity entity = mongoTemplate.findOne(query, TicketEntity.class);
        if (entity != null && entity.getProtocolNumber() != null) {
            String lastProtocol = entity.getProtocolNumber();
            int lastNumber = Integer.parseInt(lastProtocol.substring(4)); // Remove o "YYYY" do início
            return String.format("%s%06d", year, lastNumber + 1); // Adiciona 1 ao número
        } else {
            return year + "000001";
        }
    }

//            // Método para obter e incrementar a sequência
//            @Transactional
//            public int getNextSequenceNumber (String year){
//            // Query para buscar o documento do ano corrente
//            Query query = new Query(Criteria.where("year").is(year));
//
//            // Atualizar e retornar o próximo número de sequência
//            Update update = new Update().inc("sequence", 1); // Incrementa a sequência
//            FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
//
//            // Executa a operação atômica findAndModify
//            ProtocolSequence protocolSequence = mongoTemplate.findAndModify(query, update, options, ProtocolSequence.class);
//
//            if (protocolSequence == null) {
//                // Caso o ano não tenha um registro, cria um novo com o contador inicial 1
//                protocolSequence = new ProtocolSequence(year, 1);
//                mongoTemplate.save(protocolSequence);
//            }
//
//            return protocolSequence.getSequence();
//        }
}