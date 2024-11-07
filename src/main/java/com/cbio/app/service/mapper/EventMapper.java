package com.cbio.app.service.mapper;

import com.cbio.app.entities.EventEntity;
import com.cbio.core.v1.dto.google.EventDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EventMapper extends MapperBase<EventEntity, EventDTO>{

    @InheritInverseConfiguration(name = "toDto")
    @Mapping(target = "company", ignore = true)
    void fromDto(EventDTO dto, @MappingTarget EventEntity entity);
}
