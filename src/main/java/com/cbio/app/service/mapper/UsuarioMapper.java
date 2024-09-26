package com.cbio.app.service.mapper;

import com.cbio.app.entities.UsuarioEntity;
import com.cbio.core.v1.dto.UsuarioDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UsuarioMapper extends MapperBase<UsuarioEntity, UsuarioDTO>{

    @InheritInverseConfiguration(name = "toDto")
    void fromDto(UsuarioDTO dto, @MappingTarget UsuarioEntity entity);
}
