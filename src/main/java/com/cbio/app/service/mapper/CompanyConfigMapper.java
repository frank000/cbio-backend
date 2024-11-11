package com.cbio.app.service.mapper;

import com.cbio.app.entities.CompanyConfigEntity;
import com.cbio.core.v1.dto.CompanyConfigDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CompanyConfigMapper extends MapperBase<CompanyConfigEntity, CompanyConfigDTO>{

    @InheritInverseConfiguration(name = "toDto")
    @Mapping(target = "companyId", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void fromDto(CompanyConfigDTO dto, @MappingTarget CompanyConfigEntity entity);

    @Mapping(target = "companyId", ignore = true)
    CompanyConfigEntity toEntity(CompanyConfigDTO dto);
}
