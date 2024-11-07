package com.cbio.app.repository;

import com.cbio.app.entities.ResourceEntity;
import com.cbio.core.v1.dto.notification.NotificationJobDTO;
import lombok.RequiredArgsConstructor;
import org.bson.BsonNull;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ResourceCustomRepository {

    private final MongoTemplate mongoTemplate;

    public List<NotificationJobDTO> getEventsToNotify() {
        List<AggregationOperation> aggregations = new ArrayList<>();

        AggregationOperation addFieldsOperation = context -> {
            return new Document("$match",
                    new Document("$and", Arrays.asList(new Document("notifications",
                                    new Document("$ne",
                                            new BsonNull())),
                            new Document("$expr",
                                    new Document("$gt", Arrays.asList(new Document("$size", "$notifications"), 0L))))));
        };
        aggregations.add(addFieldsOperation);


        // Lookup Operation
        aggregations.add(
                Aggregation.lookup(
                        "event",
                        "dairyName",
                        "dairyName",
                        "events"
                )
        );
        AggregationOperation exprOperation = context -> {
            return new Document("$match",
                    new Document("$and", Arrays.asList(new Document("$expr",
                                    new Document("$gt", Arrays.asList(new Document("$size", "$events"), 0L))),
                            new Document("events.notified", false))));
        };
        aggregations.add(exprOperation);

        // Project Operation
        aggregations.add(
                Aggregation.project("email", "dairyName", "company", "title", "location", "description", "notifications")
                        .and(ArrayOperators.Filter.filter("events")
                                .as("event")
                                .by(ComparisonOperators.Eq.valueOf("event.notified").equalToValue(false))
                        ).as("events")
        );

        Aggregation aggregation = Aggregation.newAggregation(aggregations);
        return mongoTemplate.aggregateStream(aggregation, ResourceEntity.class, NotificationJobDTO.class).collect(Collectors.toList());
    }

}