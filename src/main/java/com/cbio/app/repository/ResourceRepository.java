package com.cbio.app.repository;

import com.cbio.app.entities.ResourceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends MongoRepository<ResourceEntity, String> {


    @Query(value = "{'company._id': ?0}", fields = "{id : 1 , description : 1, title:  1 , dairyName : 1, color: 1}")
    List<ResourceEntity> getResourcesByCompanyId(String companyId);

    Optional<ResourceEntity> getResourceByCompanyIdAndDairyNameIgnoreCase(String id, String dairyName);

    List<ResourceEntity> getAllByMorningIsNotNullOrAfternoonIsNotNullOrNightIsNotNullOrDawnIsNotNull();
}
