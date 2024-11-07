package com.cbio.app.repository.grid;

import com.cbio.app.base.repository.AbstractRepositoryGrid;
import com.cbio.core.v1.dto.ResourceFiltroGridDTO;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ResourceGridRepository extends AbstractRepositoryGrid<ResourceFiltroGridDTO> {
    public static final String OPTIONS_CASE_INSENSITIVE = "i";
    private final MongoTemplate mongoTemplate;

    @Override
    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Override
    protected void addProjectionAndMatch(ResourceFiltroGridDTO filtroDTO, List<AggregationOperation> aggregations) {
        aggregations.add(
                Aggregation.match(
                        Criteria.where("active")
                                .is(Boolean.TRUE)));
        if (filtroDTO != null && filtroDTO.getBusca() != null) {

            aggregations.add(
                    Aggregation.match(
                            Criteria.where("dairyName")
                                    .regex(filtroDTO.getBusca(),
                                            OPTIONS_CASE_INSENSITIVE)
                                    .orOperator(Criteria.where("email").regex(filtroDTO.getBusca(),
                                            OPTIONS_CASE_INSENSITIVE))
                    )

            );
        }
        if (filtroDTO != null && filtroDTO.getIdCompany() != null) {

            aggregations.add(
                    Aggregation.match(
                            Criteria.where("company._id")
                                    .is(new ObjectId(filtroDTO.getIdCompany()))));
        }

    }

    @Override
    protected Sort getSort(Pageable pageable) {
        return super.getSort(pageable);
    }
}
