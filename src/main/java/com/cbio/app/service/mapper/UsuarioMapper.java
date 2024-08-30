package com.cbio.app.service.mapper;

import com.cbio.app.entities.AttendantEntity;
import com.cbio.app.entities.UsuarioEntity;
import com.cbio.core.v1.dto.AttendantDTO;
import com.cbio.core.v1.dto.UsuarioDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper extends MapperBase<UsuarioEntity, UsuarioDTO>{


}
