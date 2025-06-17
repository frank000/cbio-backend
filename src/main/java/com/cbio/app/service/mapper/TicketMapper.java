package com.cbio.app.service.mapper;

import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.TicketEntity;
import com.cbio.core.v1.dto.TicketDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketMapper extends MapperBase<TicketEntity, TicketDTO> {


    @InheritInverseConfiguration(name = "toDto")
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "ticketMessages", expression = "java(handlerMessagesUpdate(entity, dto))")
    @Mapping(target = "id", ignore = true)
    void fromDto(TicketDTO dto, @MappingTarget TicketEntity entity);

    @Mapping(target = "ticketMessages", expression = "java(handlerMessages(dto))")
    TicketEntity toEntity(TicketDTO dto);


    default List<TicketEntity.TicketMessageDTO> handlerMessages(TicketDTO dto) {
        if (CollectionUtils.isEmpty(dto.getTicketMessages())) {
            dto.setTicketMessages(new ArrayList<>());
        }

        return dto.getTicketMessages();
    }

    default List<TicketEntity.TicketMessageDTO> handlerMessagesUpdate(TicketEntity entity, TicketDTO dto) {
        if (CollectionUtils.isEmpty(entity.getTicketMessages())) {
            entity.setTicketMessages(new ArrayList<>());
        }
        entity.getTicketMessages().add(
                TicketEntity.TicketMessageDTO.builder()
                        .message(dto.getTicketMessage())
                        .fromCompany(dto.getFromCompany())
                        .createdAt(CbioDateUtils.LocalDateTimes.now())
                        .userId(dto.getUserId())
                        .imagem(dto.getImagem())
                        .build()
        );
        return entity.getTicketMessages();
    }
}
