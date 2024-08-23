package com.cbio.chat.services;

import com.cbio.chat.repositories.ChatChannelCustomRepository;
import com.google.common.collect.Lists;
import com.cbio.chat.dto.ChatChannelInitializationDTO;
import com.cbio.chat.dto.ChatMessageDTO;
import com.cbio.chat.dto.NotificationDTO;
import com.cbio.chat.exceptions.IsSameUserException;
import com.cbio.chat.exceptions.UserNotFoundException;
import com.cbio.chat.interfaces.IChatService;
import com.cbio.chat.mappers.ChatMessageMapper;
import com.cbio.chat.models.ChatChannelEntity;
import com.cbio.chat.models.ChatMessageEntity;
import com.cbio.chat.models.UserChatEntity;
import com.cbio.chat.repositories.ChatChannelRepository;
import com.cbio.chat.repositories.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService implements IChatService {
    private final ChatChannelRepository chatChannelRepository;

    private final ChatMessageRepository chatMessageRepository;

    private final UserService userService;
    private final ChatChannelCustomRepository chatChannelCustomRepository;

    private final int MAX_PAGABLE_CHAT_MESSAGES = 100;



    private String getExistingChannel(ChatChannelInitializationDTO chatChannelInitializationDTO) {
        List<ChatChannelEntity> channel = chatChannelCustomRepository
                .findUsers(
                        chatChannelInitializationDTO.getUserIdOne(),
                        chatChannelInitializationDTO.getUserIdTwo()
                );

        return (channel != null && !channel.isEmpty()) ? channel.get(0).getId() : null;
    }

    private String newChatSession(ChatChannelInitializationDTO chatChannelInitializationDTO)
            throws BeansException, UserNotFoundException {
        ChatChannelEntity channel = new ChatChannelEntity(
                userService.getUser(chatChannelInitializationDTO.getUserIdOne()),
                userService.getUser(chatChannelInitializationDTO.getUserIdTwo())
        );

        chatChannelRepository.save(channel);

        return channel.getId();
    }

    public String establishChatSession(ChatChannelInitializationDTO chatChannelInitializationDTO)
            throws IsSameUserException, BeansException, UserNotFoundException {
        if (chatChannelInitializationDTO.getUserIdOne() == chatChannelInitializationDTO.getUserIdTwo()) {
            throw new IsSameUserException();
        }

        String uuid = getExistingChannel(chatChannelInitializationDTO);

        // If channel doesn't already exist, create a new one
        return (uuid != null) ? uuid : newChatSession(chatChannelInitializationDTO);
    }

    public void submitMessage(String channelId, ChatMessageDTO chatMessageDTO)
            throws BeansException, UserNotFoundException {
        ChatMessageEntity chatMessage = ChatMessageMapper.mapChatDTOtoMessage(chatMessageDTO);

        chatMessageRepository.save(chatMessage);

        UserChatEntity fromUser = userService.getUser(chatMessage.getAuthorUser().getId());
        UserChatEntity recipientUser = userService.getUser(chatMessage.getRecipientUser().getId());

        userService.notifyUser(recipientUser,
                new NotificationDTO(
                        "ChatMessageNotification",
                        fromUser.getUsername() + " has sent you a message",
                        chatMessage.getAuthorUser().getId()
                )
        );
    }

    public List<ChatMessageDTO> getExistingChatMessages(String channelUuid) {
        ChatChannelEntity channel = chatChannelRepository.getChannelDetails(channelUuid);

        List<ChatMessageEntity> chatMessages =
                chatMessageRepository.getExistingChatMessages(
                        channel.getUserOne().getId(),
                        channel.getUserTwo().getId(),
                        PageRequest.of(0, MAX_PAGABLE_CHAT_MESSAGES)
                );

        // TODO: fix this
        List<ChatMessageEntity> messagesByLatest = Lists.reverse(chatMessages);

        return ChatMessageMapper.mapMessagesToChatDTOs(messagesByLatest);
    }
}