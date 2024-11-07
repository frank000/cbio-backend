package com.cbio.app.service.mapper;

import com.cbio.app.entities.ModelEntity;
import com.cbio.core.v1.dto.ModelDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ModelMapper extends MapperBase<ModelEntity, ModelDTO> {

    @InheritInverseConfiguration(name = "toDto")
    void fromDto(ModelDTO dto, @MappingTarget ModelEntity entity);
}
