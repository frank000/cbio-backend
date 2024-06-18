package com.policia.df.bot.app.service.mapper;

import com.policia.df.bot.app.entities.ComandoEntity;
import com.policia.df.bot.app.entities.EtapaEntity;
import com.policia.df.bot.core.v1.dto.ComandoDTO;
import com.policia.df.bot.core.v1.dto.EtapaDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface EtapaMapper {

    EtapaMapper INSTANCE = Mappers.getMapper( EtapaMapper.class );

    EtapaEntity etapaDTOToEtapaEntity(EtapaDTO dto, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    EtapaDTO etapaEntityToEtapaDTO(EtapaEntity entity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    List<EtapaDTO> listEtapaEntityToListEtapaDTO(List<EtapaEntity> listaEntity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);
}
