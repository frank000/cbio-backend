package com.cbio.app.service.mapper;

import com.cbio.app.entities.PhraseEntity;
import com.cbio.app.entities.TierEntity;
import com.cbio.core.v1.dto.PhraseDTO;
import com.cbio.core.v1.dto.TierDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TierMapper extends MapperBase<TierEntity, TierDTO>{


//
//    @InheritInverseConfiguration(name = "toDto")
//    void fromDto(PhraseDTO dto, @MappingTarget PhraseEntity entity);
}
