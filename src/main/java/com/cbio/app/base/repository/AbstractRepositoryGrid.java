package com.cbio.app.base.repository;

import com.cbio.app.base.grid.PageableResponse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.util.CloseableIterator;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public abstract class AbstractRepositoryGrid<F extends FiltroDTOInterface> {

    public static final String TOTAL = "total";

    protected abstract MongoTemplate getMongoTemplate();

    protected abstract void addProjectionAndMatch(final F filtroDTO, final List<AggregationOperation> aggregations);

    private Long obtemTotalGrid(final F filtroDTO, String document) {

        List<AggregationOperation> aggregations = new ArrayList<>();

        addProjectionAndMatch(filtroDTO, aggregations);

        aggregations.add(Aggregation.count().as(TOTAL));

        Map<?, ?> map = getMongoTemplate().aggregate(Aggregation.newAggregation(aggregations), document, Map.class)
                .getUniqueMappedResult();

        Long total;
        if (map == null || map.get(TOTAL) == null) {
            total = 0L;
        } else {
            total = ((Number) map.get(TOTAL)).longValue();
        }

        return total;
    }

    public final <T extends Serializable> PageableResponse<T> obtemGrid(final F filtroDTO, final Pageable pageable, Class<?> entity, Class<T> classeRetorno) {

        Document document = entity.getAnnotation(Document.class);
        return obtemGrid(filtroDTO, pageable, document.value(), classeRetorno);
    }

    public final <T extends Serializable> PageableResponse<T> obtemGrid(final F filtroDTO, final Pageable pageable, String document, Class<T> classeRetorno) {

        Long total = obtemTotalGrid(filtroDTO, document);
        List<T> gridDTOList;
        if (total.intValue() == 0) {
            gridDTOList = Collections.emptyList();
        } else {

            Aggregation aggregation = getGridAggregation(filtroDTO, pageable);

            gridDTOList = getMongoTemplate()
                    .aggregate(aggregation, document, classeRetorno)
                    .getMappedResults();
        }

        return PageableResponse.of(total, gridDTOList);
    }

    public final <T extends Serializable> CloseableIterator<T> obtemCloseableIteratorGrid(final F filtroDTO, final Pageable pageable, Class<?> entity, Class<T> classeRetorno) {
        Document document = entity.getAnnotation(Document.class);
        return obtemCloseableIteratorGrid(filtroDTO, pageable, document.value(), classeRetorno);
    }

    public final <T extends Serializable> CloseableIterator<T> obtemCloseableIteratorGrid(final F filtroDTO, final Pageable pageable, String document, Class<T> classeRetorno) {

        Aggregation aggregation = getGridAggregation(filtroDTO, pageable);

        return (CloseableIterator<T>) getMongoTemplate().aggregateStream(aggregation, document, classeRetorno);
    }

    private Aggregation getGridAggregation(final F filtroDTO, final Pageable pageable) {

        List<AggregationOperation> aggregations = new ArrayList<>();

        addProjectionAndMatch(filtroDTO, aggregations);

        if (pageable != null) {

            if (getSort(pageable) != Sort.unsorted()) {
                aggregations.add(Aggregation.sort(getSort(pageable)));
            }

            if (pageable.getPageSize() != 0) {
                aggregations.add(Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()));
                aggregations.add(Aggregation.limit(pageable.getPageSize()));
            }
        }

        return Aggregation.newAggregation(aggregations);
    }

    protected Sort getSort(final Pageable pageable) {
        return pageable.getSort();
    }
}

