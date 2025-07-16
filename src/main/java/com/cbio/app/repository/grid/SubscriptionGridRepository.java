package com.cbio.app.repository.grid;

import com.cbio.app.base.repository.AbstractRepositoryGrid;
import com.cbio.core.service.AuthService;
import com.cbio.core.v1.dto.AttendantFiltroGridDTO;
import com.cbio.core.v1.dto.SubscriptionFiltroGridDTO;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubscriptionGridRepository extends AbstractRepositoryGrid<SubscriptionFiltroGridDTO> {
    public static final String OPTIONS_CASE_INSENSITIVE = "i";
    private final MongoTemplate mongoTemplate;
    private final AuthService authService;

    @Override
    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Override
    protected void addProjectionAndMatch(SubscriptionFiltroGridDTO filtroDTO, List<AggregationOperation> aggregations) {
//        String companyIdUserLogged = authService.getCompanyIdUserLogged();

        aggregations.add(Aggregation.match(Criteria.where("active").is(Boolean.TRUE)));

         if(StringUtils.hasText(filtroDTO.getIdCompany())){


            aggregations.add(
                    Aggregation.match(
                            Criteria.where("companyId")
                                    .is(filtroDTO.getIdCompany() )));


        }

        if (filtroDTO != null && filtroDTO.getBusca() != null ) {

            aggregations.add(
                    Aggregation.match(
                            Criteria.where("name")
                                    .regex(filtroDTO.getBusca(),
                                            OPTIONS_CASE_INSENSITIVE)
                                    .orOperator(
                                            Criteria.where("subscriptionId")
                                                    .regex(filtroDTO.getBusca(),
                                                            OPTIONS_CASE_INSENSITIVE),

                                            Criteria.where("email")
                                                    .regex(filtroDTO.getBusca(),
                                                            OPTIONS_CASE_INSENSITIVE)
                                    )
                    )

            );
        }

    }

    @Override
    protected Sort getSort(Pageable pageable) {
        return super.getSort(pageable);
    }
}
