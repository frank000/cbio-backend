package com.cbio.app.service.mapper;

import com.cbio.app.entities.ComandoEntity;
import com.cbio.core.v1.dto.ComandoDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComandoMapper {

    ComandoMapper INSTANCE = Mappers.getMapper( ComandoMapper.class );

    ComandoEntity comandoDTOToComandoEntity(ComandoDTO dto, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    ComandoDTO comandoEntityToComandoDTO(ComandoEntity entity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    List<ComandoDTO> listComandoEntityToListComandoDTO(List<ComandoEntity> listaEntity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);
}
