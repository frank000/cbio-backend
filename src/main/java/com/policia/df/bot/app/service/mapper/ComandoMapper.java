package com.policia.df.bot.app.service.mapper;

import com.policia.df.bot.app.entities.ComandoEntity;
import com.policia.df.bot.core.v1.dto.ComandoDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper
public interface ComandoMapper {

    ComandoMapper INSTANCE = Mappers.getMapper( ComandoMapper.class );

    ComandoEntity comandoDTOToComandoEntity(ComandoDTO dto, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    ComandoDTO comandoEntityToComandoDTO(ComandoEntity entity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);


}
