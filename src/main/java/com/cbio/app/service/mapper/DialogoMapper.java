package com.cbio.app.service.mapper;

import com.cbio.app.entities.CanalEntity;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.chat.models.DialogoEntity;
import com.cbio.core.v1.dto.CanalDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DialogoMapper extends MapperBase<DialogoEntity, DialogoDTO>{

    @InheritInverseConfiguration(name = "toDto")
    void fromDto(DialogoDTO dto, @MappingTarget DialogoEntity entity);
}
