package com.cbio.app.repository.grid;

import com.cbio.app.base.repository.AbstractRepositoryGrid;
import com.cbio.core.v1.dto.ModelFiltroGridDTO;
import com.cbio.core.v1.dto.PhraseFiltroGridDTO;
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
public class TemplateGridRepository extends AbstractRepositoryGrid<ModelFiltroGridDTO> {
    public static final String OPTIONS_CASE_INSENSITIVE = "i";
    private final MongoTemplate mongoTemplate;
    @Override
    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Override
    protected void addProjectionAndMatch(ModelFiltroGridDTO filtroDTO, List<AggregationOperation> aggregations) {
        if (filtroDTO != null && filtroDTO.getBusca() != null ) {

            aggregations.add(
                    Aggregation.match(
                            Criteria.where("name")
                                    .regex(filtroDTO.getBusca(),
                                            OPTIONS_CASE_INSENSITIVE)
                    )

            );
        }
        if(filtroDTO != null && filtroDTO.getIdCompany() != null){

            aggregations.add(
                    Aggregation.match(
                            Criteria.where("company._id")
                                    .is(new ObjectId(filtroDTO.getIdCompany()) )));
        }

    }

    @Override
    protected Sort getSort(Pageable pageable) {
        return super.getSort(pageable);
    }
}
