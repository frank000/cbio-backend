package com.cbio.app.service.mapper;

import com.cbio.app.entities.AttendantEntity;
import com.cbio.core.v1.dto.AttendantDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttendantMapper extends MapperBase<AttendantEntity, AttendantDTO>{


}
