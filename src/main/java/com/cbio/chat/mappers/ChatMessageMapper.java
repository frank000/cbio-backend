package com.cbio.chat.mappers;

import com.cbio.chat.dto.ChatMessageDTO;
import com.cbio.chat.models.ChatMessageEntity;
import com.cbio.chat.models.UserChatEntity;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageMapper {
  public static List<ChatMessageDTO> mapMessagesToChatDTOs(List<ChatMessageEntity> chatMessages) {
    List<ChatMessageDTO> dtos = new ArrayList<ChatMessageDTO>();

    for(ChatMessageEntity chatMessage : chatMessages) {
      dtos.add(
        new ChatMessageDTO(
          chatMessage.getContents(),
          chatMessage.getAuthorUser().getId(),
          chatMessage.getRecipientUser().getId()
        )
      );
    }

    return dtos;
  }

  public static ChatMessageEntity mapChatDTOtoMessage(ChatMessageDTO dto) {
    return new ChatMessageEntity(

      // only need the id for mapping
       UserChatEntity.builder().uuid(dto.getFromUserId()).build(),
       UserChatEntity.builder().uuid(dto.getToUserId()).build(),

      dto.getContents()
    );
  }
}
