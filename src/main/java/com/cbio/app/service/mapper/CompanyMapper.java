package com.cbio.app.service.mapper;

import com.cbio.app.entities.CompanyEntity;
import com.cbio.app.entities.UsuarioEntity;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.UsuarioDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper extends MapperBase<CompanyEntity, CompanyDTO>{


}
