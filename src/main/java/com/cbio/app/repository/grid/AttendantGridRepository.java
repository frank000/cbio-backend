package com.cbio.app.repository.grid;

import com.cbio.app.base.repository.AbstractRepositoryGrid;
import com.cbio.core.service.AuthService;
import com.cbio.core.v1.dto.AttendantFiltroGridDTO;
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
public class AttendantGridRepository extends AbstractRepositoryGrid<AttendantFiltroGridDTO> {
    public static final String OPTIONS_CASE_INSENSITIVE = "i";
    private final MongoTemplate mongoTemplate;
    private final AuthService authService;

    @Override
    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Override
    protected void addProjectionAndMatch(AttendantFiltroGridDTO filtroDTO, List<AggregationOperation> aggregations) {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();

        aggregations.add(Aggregation.match(Criteria.where("active").is(Boolean.TRUE)));

        if(StringUtils.hasText(companyIdUserLogged)) {

            aggregations.add(
                    Aggregation.match(
                            Criteria.where("company._id")
                                    .is(new ObjectId(companyIdUserLogged) )));
        }else if(StringUtils.hasText(filtroDTO.getIdCompany())){


            aggregations.add(
                    Aggregation.match(
                            Criteria.where("company._id")
                                    .is(new ObjectId(filtroDTO.getIdCompany()) )));


        }

        if (filtroDTO != null && filtroDTO.getBusca() != null ) {

            aggregations.add(
                    Aggregation.match(
                            Criteria.where("name")
                                    .regex(filtroDTO.getBusca(),
                                            OPTIONS_CASE_INSENSITIVE)
                    )

            );
        }

        if(filtroDTO != null && filtroDTO.getPerfil() != null){
            aggregations.add(
                    Aggregation.match(
                            Criteria.where("perfil")
                                    .in(filtroDTO.getPerfil())
                    )

            );
        }
    }

    @Override
    protected Sort getSort(Pageable pageable) {
        return super.getSort(pageable);
    }
}
