package com.cbio.chat.mappers;

import com.cbio.app.entities.AttendantEntity;
import com.cbio.app.service.mapper.MapperBase;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.chat.dto.UserDTO;
import com.cbio.chat.models.DialogoEntity;
import com.cbio.chat.models.UserChatEntity;
import com.cbio.core.v1.dto.AttendantDTO;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DialogoMapper extends MapperBase<DialogoEntity, DialogoDTO> {

}