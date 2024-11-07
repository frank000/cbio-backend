package com.cbio.app.service.mapper;

import com.cbio.app.entities.ContactEntity;
import com.cbio.core.v1.dto.ContactDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContactMapper extends MapperBase<ContactEntity, ContactDTO> {


    @InheritInverseConfiguration(name = "toDto")
    @Mapping(target = "company", ignore = true)
    void fromDto(ContactDTO dto, @MappingTarget ContactEntity entity);
}
