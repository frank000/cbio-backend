package com.cbio.app.repository.grid;

import com.cbio.app.base.repository.AbstractRepositoryGrid;
import com.cbio.core.service.AuthService;
import com.cbio.core.v1.dto.ContactFiltroGridDTO;
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
public class ContactsGridRepository extends AbstractRepositoryGrid<ContactFiltroGridDTO> {
    public static final String OPTIONS_CASE_INSENSITIVE = "i";
    private final MongoTemplate mongoTemplate;
    private final AuthService authService;

    @Override
    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Override
    protected void addProjectionAndMatch(ContactFiltroGridDTO filtroDTO, List<AggregationOperation> aggregations) {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();

        aggregations.add(Aggregation.match(Criteria.where("active").is(Boolean.TRUE)));

        if (StringUtils.hasText(companyIdUserLogged)) {
            Criteria criteria = Criteria.where("company._id").is(new ObjectId(companyIdUserLogged));
            aggregations.add(
                    Aggregation.match(criteria)
            );
        } else if (StringUtils.hasText(filtroDTO.getIdCompany())) {
            Criteria criteria = Criteria.where("company._id").is(new ObjectId(filtroDTO.getIdCompany()));
            aggregations.add(
                    Aggregation.match(criteria)
            );
        }

        if (filtroDTO != null && filtroDTO.getBusca() != null) {

            aggregations.add(
                    Aggregation.match(
                            Criteria.where("name")
                                    .regex(filtroDTO.getBusca(),
                                            OPTIONS_CASE_INSENSITIVE)
                    )

            );
        }
    }

    @Override
    protected Sort getSort(Pageable pageable) {
        return super.getSort(pageable);
    }
}
