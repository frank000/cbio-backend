package com.cbio.app.service.mapper;

import com.cbio.app.entities.ResourceEntity;
import com.cbio.core.v1.dto.ResourceDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ResourceMapper extends MapperBase<ResourceEntity, ResourceDTO>{

    @InheritInverseConfiguration(name = "toDto")
    @Mapping(target = "company", ignore = true)
    void fromDto(ResourceDTO dto, @MappingTarget ResourceEntity entity);
}
