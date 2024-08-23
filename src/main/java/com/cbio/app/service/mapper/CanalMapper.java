package com.cbio.app.service.mapper;

import com.cbio.app.entities.CanalEntity;
import com.cbio.core.v1.dto.CanalDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CanalMapper {

    CanalMapper INSTANCE = Mappers.getMapper( CanalMapper.class );

    CanalEntity canalDTOToCanalEntity(CanalDTO dto, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    CanalDTO canalEntityToCanalDTO(CanalEntity entity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    List<CanalDTO> listCanalEntityToListCanalDTO(List<CanalEntity> listaEntity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

}
