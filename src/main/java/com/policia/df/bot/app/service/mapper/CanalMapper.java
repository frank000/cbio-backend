package com.policia.df.bot.app.service.mapper;

import com.policia.df.bot.app.entities.CanalEntity;
import com.policia.df.bot.core.v1.dto.CanalDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CanalMapper {

    CanalMapper INSTANCE = Mappers.getMapper( CanalMapper.class );

    CanalEntity canalDTOToCanalEntity(CanalDTO dto, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    CanalDTO canalEntityToCanalDTO(CanalEntity entity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    List<CanalDTO> listCanalEntityToListCanalDTO(List<CanalEntity> listaEntity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

}
