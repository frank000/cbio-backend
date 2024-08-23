package com.cbio.chat.mappers;

import com.cbio.chat.dto.UserDTO;
import com.cbio.chat.models.UserChatEntity;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
  public static List<UserDTO> mapUsersToUserDTOs(List<UserChatEntity> users) {
    List<UserDTO> dtos = new ArrayList<UserDTO>();

    for(UserChatEntity user : users) {
      dtos.add(
        new UserDTO(
          user.getId(),
          user.getUsername()
        )
      );
    }

    return dtos;
  }
}