package com.cbio.app.service.mapper;

import com.cbio.app.entities.PhraseEntity;
import com.cbio.core.v1.dto.PhraseDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PhraseMapper extends MapperBase<PhraseEntity, PhraseDTO>{



    @InheritInverseConfiguration(name = "toDto")
    void fromDto(PhraseDTO dto, @MappingTarget PhraseEntity entity);
}
