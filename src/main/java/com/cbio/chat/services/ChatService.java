package com.cbio.chat.services;

import com.cbio.app.entities.SessaoEntity;
import com.cbio.chat.dto.ChatChannelInitializationDTO;
import com.cbio.chat.dto.ChatMessageDTO;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.chat.dto.NotificationDTO;
import com.cbio.chat.exceptions.IsSameUserException;
import com.cbio.chat.exceptions.UserNotFoundException;
import com.cbio.chat.interfaces.IChatService;
import com.cbio.chat.mappers.ChatMessageMapper;
import com.cbio.chat.models.ChatChannelEntity;
import com.cbio.chat.models.ChatMessageEntity;
import com.cbio.chat.models.UserChatEntity;
import com.cbio.chat.repositories.ChatChannelCustomRepository;
import com.cbio.chat.repositories.ChatChannelRepository;
import com.cbio.chat.repositories.ChatMessageRepository;
import com.cbio.core.service.AttendantService;
import com.cbio.core.service.ChatbotForwardService;
import com.cbio.core.service.SessaoService;
import com.cbio.core.v1.dto.EntradaMensagemDTO;
import com.cbio.core.v1.dto.UsuarioDTO;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService implements IChatService {
    private final ChatChannelRepository chatChannelRepository;

    private final ChatMessageRepository chatMessageRepository;

    private final UserChatService userService;

    private final SessaoService sessaoService;


    private final AttendantService attendantService;

    private final ChatChannelCustomRepository chatChannelCustomRepository;

    private final int MAX_PAGABLE_CHAT_MESSAGES = 100;

    private final ChatbotForwardService forwardService;


    private String getExistingChannel(ChatChannelInitializationDTO chatChannelInitializationDTO) {
        List<ChatChannelEntity> channel = chatChannelCustomRepository
                .findUsers(
                        chatChannelInitializationDTO.getUserIdOne(),
                        chatChannelInitializationDTO.getUserIdTwo()
                );

        return (channel != null && !channel.isEmpty()) ? channel.get(0).getId() : null;
    }


    //TODO Colocar no Reedis
    public ChatChannelInitializationDTO getChatChannelInitializationDTO(String channelId) {
        ChatChannelEntity chatChannelEntity = chatChannelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Chat não encontrado."));

        return ChatChannelInitializationDTO.builder()
                .userIdOne(chatChannelEntity.getUserOne().getUuid())
                .userIdTwo(chatChannelEntity.getUserTwo().getUuid())
                .initCanal(chatChannelEntity.getInitCanal())
                .build();
    }

    private String newChatSession(ChatChannelInitializationDTO chatChannelInitializationDTO)
            throws BeansException, UserNotFoundException {
        ChatChannelEntity channel = new ChatChannelEntity(
                userService.getUser(chatChannelInitializationDTO.getUserIdOne()),
                userService.getUser(chatChannelInitializationDTO.getUserIdTwo()),
                chatChannelInitializationDTO.getInitCanal()
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
        ChatChannelEntity channel = chatChannelRepository.findById(channelUuid)
                .orElseThrow(() -> new RuntimeException("Chat não encontrado."));

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


    public void receiveMessageAttendant(EntradaMensagemDTO entradaMensagemDTO, String channelId, String attendantId){

        SessaoEntity sessaoEntity = sessaoService.buscaSessaoAtivaPorUsuarioCanal(
                Long.valueOf(entradaMensagemDTO.getIdentificadorRemetente()),
                entradaMensagemDTO.getCanal().getNome(),
                channelId);

        DialogoDTO dialogoDTO = DialogoDTO.builder()
                .mensagem(formatAnswearToClient(entradaMensagemDTO, attendantId))
                .identificadorRemetente(entradaMensagemDTO.getIdentificadorRemetente())
                .canal(sessaoEntity.getCanal())
                .channelUuid(channelId)
                .build();

        forwardService.enviaRespostaDialogoPorCanal(entradaMensagemDTO.getCanal(), dialogoDTO);



    }

    private String formatAnswearToClient(EntradaMensagemDTO entradaMensagemDTO, String attendantId) {
        UsuarioDTO attendantDTO = attendantService.buscaPorId(attendantId);

        StringBuilder sb = new StringBuilder();
        sb.append("*").append(attendantDTO.getName())
                .append("*\n");
        sb.append(entradaMensagemDTO.getMensagem());

        return sb.toString();
    }
}