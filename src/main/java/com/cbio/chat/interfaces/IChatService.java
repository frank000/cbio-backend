package com.cbio.chat.interfaces;

import com.cbio.chat.dto.ChatChannelInitializationDTO;
import com.cbio.chat.dto.ChatMessageDTO;
import com.cbio.chat.exceptions.IsSameUserException;
import com.cbio.chat.exceptions.UserNotFoundException;
import org.springframework.beans.BeansException;

import java.time.LocalDateTime;
import java.util.List;

public interface IChatService {
  String establishChatSession(ChatChannelInitializationDTO chatChannelInitializationDTO, LocalDateTime initTime)
      throws IsSameUserException, BeansException, UserNotFoundException;

  void submitMessage(String channelId, ChatMessageDTO chatMessageDTO)
      throws BeansException, UserNotFoundException;
  
  List<ChatMessageDTO> getExistingChatMessages(String channelUuid);

  ChatChannelInitializationDTO getChatChannelInitializationDTO(String channelId);
}