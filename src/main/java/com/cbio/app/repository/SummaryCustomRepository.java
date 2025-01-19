package com.cbio.app.repository;

import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.UsuarioEntity;
import com.cbio.core.v1.dto.SummaryDTO;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SummaryCustomRepository {

    private final MongoTemplate mongoTemplate;

    public List<SummaryDTO> getSumaryByCompanyAndPeriod(String companyId, String dateInitString, String dateEndString) {
        List<AggregationOperation> aggregations = new ArrayList<>();

        // Match Operation
        aggregations.add(
                Aggregation.match(
                        Criteria.where("company._id").is(new ObjectId(companyId))
                                .and("perfil").is("ATTENDANT")
                                .and("active").is(true)
                )
        );

        // Add Fields Operation
        aggregations.add(
                Aggregation.addFields()
                        .addField("stringId")
                        .withValue(new Document("$toString", "$_id"))
                        .build()
        );

        // Lookup Operation
        aggregations.add(
                Aggregation.lookup(
                        "chatChannel",
                        "stringId",
                        "userOne.uuid",
                        "channels"
                )
        );

        // Add Fields Operation with Filter
        AggregationOperation addFieldsOperation = context -> {
            Document filterExpression = new Document("$filter",
                    new Document("input", "$channels")
                            .append("as", "item")
                            .append("cond", new Document("$and", Arrays.asList(
                                    new Document("$gte", Arrays.asList(
                                            new Document("$dateFromParts",
                                                    new Document("year", new Document("$year", "$$item.initTime"))
                                                            .append("month", new Document("$month", "$$item.initTime"))
                                                            .append("day", new Document("$dayOfMonth", "$$item.initTime"))
                                            ),
                                            new Document("$dateFromString", new Document("dateString", dateInitString))
                                    )),
                                    new Document("$lte", Arrays.asList(
                                            new Document("$dateFromParts",
                                                    new Document("year", new Document("$year", "$$item.initTime"))
                                                            .append("month", new Document("$month", "$$item.initTime"))
                                                            .append("day", new Document("$dayOfMonth", "$$item.initTime"))
                                            ),
                                            new Document("$dateFromString", new Document("dateString", dateEndString))
                                    ))
                            )))
            );

            Document addFields = new Document("channelsFiltered", filterExpression);

            return new Document("$addFields", addFields);
        };
        aggregations.add(addFieldsOperation);

        aggregations.add(Aggregation.unwind("$channelsFiltered"));


        // Add Fields Operation for Size
        aggregations.add(Aggregation.addFields()
                .addField("qntAttendances")
                .withValue(new Document("$size", "$channelsFiltered.history"))
                .build()
        );

        // Project Operation
        aggregations.add(
                Aggregation.project("name", "stringId", "perfil", "active", "channels", "channelsFiltered", "qntAttendances"));


        Aggregation aggregation = Aggregation.newAggregation(aggregations);
        return mongoTemplate.aggregateStream(aggregation, UsuarioEntity.class, SummaryDTO.class).collect(Collectors.toList());
    }


    public  List<SummaryDTO>  performAggregationperMonth(String companyId) {
        List<AggregationOperation> aggregations = new ArrayList<>();

        // Match Operation
        aggregations.add(
                Aggregation.match(
                        Criteria.where("company._id").is(new ObjectId(companyId))
                                .and("perfil").is("ATTENDANT")
                                .and("active").is(true)
                )
        );

        // Add Fields Operation
        aggregations.add(
                Aggregation.addFields()
                        .addField("stringId")
                        .withValue(new Document("$toString", "$_id"))
                        .build()
        );

        // Lookup Operation
        aggregations.add(
                Aggregation.lookup(
                        "chatChannel",
                        "stringId",
                        "userOne.uuid",
                        "channels"
                )
        );
        int year = CbioDateUtils.LocalDate.now().getYear();
        // Add Fields Operation with Filter
        AggregationOperation addFieldsFilteredOperation = context -> {
            Document filterExpression = new Document("$filter",
                    new Document("input", "$channels")
                            .append("as", "item")
                            .append("cond", new Document("$eq", List.of(
                                    new Document("$year", "$$item.initTime"),
                                    year
                            )))
            );

            Document addFields = new Document("channelsFiltered", filterExpression);

            return new Document("$addFields", addFields);
        };
        aggregations.add(addFieldsFilteredOperation);

        // Unwind Operation
        aggregations.add(
                Aggregation.unwind("channelsFiltered")
        );

        // Add Fields Operation for mesNumero
        aggregations.add(
                Aggregation.addFields()
                        .addField("channelsFiltered.mesNumero")
                        .withValue(new Document("$dateToString", new Document("format", "%m").append("date", "$channelsFiltered.initTime").append("timezone", "UTC")))
                        .build()
        );

        // Add Fields Operation for mes
        AggregationOperation addFieldsMesOperation = context -> {
            Document switchExpression = new Document("$switch", new Document("branches", List.of(
                    new Document("case", new Document("$eq", List.of("$channelsFiltered.mesNumero", "01"))).append("then", "Janeiro"),
                    new Document("case", new Document("$eq", List.of("$channelsFiltered.mesNumero", "02"))).append("then", "Fevereiro"),
                    new Document("case", new Document("$eq", List.of("$channelsFiltered.mesNumero", "03"))).append("then", "Março"),
                    new Document("case", new Document("$eq", List.of("$channelsFiltered.mesNumero", "04"))).append("then", "Abril"),
                    new Document("case", new Document("$eq", List.of("$channelsFiltered.mesNumero", "05"))).append("then", "Maio"),
                    new Document("case", new Document("$eq", List.of("$channelsFiltered.mesNumero", "06"))).append("then", "Junho"),
                    new Document("case", new Document("$eq", List.of("$channelsFiltered.mesNumero", "07"))).append("then", "Julho"),
                    new Document("case", new Document("$eq", List.of("$channelsFiltered.mesNumero", "08"))).append("then", "Agosto"),
                    new Document("case", new Document("$eq", List.of("$channelsFiltered.mesNumero", "09"))).append("then", "Setembro"),
                    new Document("case", new Document("$eq", List.of("$channelsFiltered.mesNumero", "10"))).append("then", "Outubro"),
                    new Document("case", new Document("$eq", List.of("$channelsFiltered.mesNumero", "11"))).append("then", "Novembro"),
                    new Document("case", new Document("$eq", List.of("$channelsFiltered.mesNumero", "12"))).append("then", "Dezembro")
            )).append("default", "Indefinido"));

            Document addFields = new Document("channelsFiltered.mes", switchExpression);

            return new Document("$addFields", addFields);
        };
        aggregations.add(addFieldsMesOperation);
    
        // Replace Root Operation
        aggregations.add(
                Aggregation.replaceRoot("channelsFiltered")
        );

        // Add Fields Operation for Size
        aggregations.add(Aggregation.addFields()
                .addField("qntAttendances")
                .withValue(new Document("$size", "$history"))
                .build()
        );

        aggregations.add(
                Aggregation.project("mesNumero", "mes", "initCanal", "qntAttendances")
        );
        // Create Aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(aggregations);

        // Execute aggregation
        return mongoTemplate.aggregateStream(aggregation, UsuarioEntity.class, SummaryDTO.class).collect(Collectors.toList());

    }
}