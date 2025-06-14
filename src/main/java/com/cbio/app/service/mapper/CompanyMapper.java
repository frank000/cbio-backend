package com.cbio.app.service.mapper;

import com.cbio.app.entities.CompanyEntity;
import com.cbio.app.entities.UsuarioEntity;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.UsuarioDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyMapper extends MapperBase<CompanyEntity, CompanyDTO>{

    @InheritInverseConfiguration(name = "toDto")
    void fromDto(CompanyDTO dto, @MappingTarget CompanyEntity entity);

    @Override
    @Mapping(target = "statusPayment", ignore = true)
    CompanyEntity toEntity(CompanyDTO dto);
}
