package com.cbio.app.base.repository;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;


public class RepositoryUtil {
    public static class CustomAggregationOperation implements AggregationOperation {

        private Document document;

        private CustomAggregationOperation(Document document) {
            this.document = document;
        }

        @Override
        public Document toDocument(AggregationOperationContext aggregationOperationContext) {
            return aggregationOperationContext.getMappedObject(document);
        }

        public static CustomAggregationOperation of(Document document) {
            return new CustomAggregationOperation(document);
        }
    }
}
