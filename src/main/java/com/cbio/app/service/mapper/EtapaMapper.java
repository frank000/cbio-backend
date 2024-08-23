package com.cbio.app.service.mapper;

import com.cbio.app.entities.EtapaEntity;
import com.cbio.core.v1.dto.EtapaDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EtapaMapper {

    EtapaMapper INSTANCE = Mappers.getMapper( EtapaMapper.class );

    EtapaEntity etapaDTOToEtapaEntity(EtapaDTO dto, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    EtapaDTO etapaEntityToEtapaDTO(EtapaEntity entity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    List<EtapaDTO> listEtapaEntityToListEtapaDTO(List<EtapaEntity> listaEntity, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);
}
