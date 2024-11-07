package com.cbio.chat.services;

import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.service.assitents.AttendantAssistent;
import com.cbio.app.service.enuns.AssistentEnum;
import com.cbio.app.service.minio.MinioService;
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
import com.cbio.core.v1.dto.MediaDTO;
import com.cbio.core.v1.dto.UsuarioDTO;
import com.google.common.collect.Lists;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService implements IChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private final ChatChannelRepository chatChannelRepository;

    private final ChatMessageRepository chatMessageRepository;

    private final UserChatService userService;

    private final SessaoService sessaoService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final AttendantService attendantService;

    private final AttendantAssistent attendantAssistent;

    private final ChatChannelCustomRepository chatChannelCustomRepository;

    private final int MAX_PAGABLE_CHAT_MESSAGES = 100;

    private final ChatbotForwardService forwardService;

    private final MinioService minioService;

    private String getExistingChannel(ChatChannelInitializationDTO chatChannelInitializationDTO) {
        List<ChatChannelEntity> channel = chatChannelCustomRepository
                .findUsers(
                        chatChannelInitializationDTO.getUserIdOne(),
                        chatChannelInitializationDTO.getUserIdTwo(),
                        chatChannelInitializationDTO.getInitCanal()
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

    private String newChatSession(ChatChannelInitializationDTO chatChannelInitializationDTO, LocalDateTime initTime)
            throws BeansException, UserNotFoundException {
        ChatChannelEntity channel = new ChatChannelEntity(
                userService.getUser(chatChannelInitializationDTO.getUserIdOne()),
                userService.getUser(chatChannelInitializationDTO.getUserIdTwo()),
                chatChannelInitializationDTO.getInitCanal(),
                initTime
        );

        chatChannelRepository.save(channel);

        return channel.getId();
    }

    public String establishChatSession(ChatChannelInitializationDTO chatChannelInitializationDTO, LocalDateTime initTime)
            throws IsSameUserException, BeansException, UserNotFoundException {
        if (chatChannelInitializationDTO.getUserIdOne().equals(chatChannelInitializationDTO.getUserIdTwo())) {
            throw new IsSameUserException();
        }

        String uuid = getExistingChannel(chatChannelInitializationDTO);

        // If channel doesn't already exist, create a new one
        return (uuid != null) ? uuid : newChatSession(chatChannelInitializationDTO, initTime);
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


    /**
     * Resposavel por reCEBER A MESAGEM vinda direto da pagina do front end enviada pelo atendente
     * Faz o tratamentos
     * @param entradaMensagemDTO
     * @param channelId
     * @param attendantId
     * @throws Exception
     */
    public void receiveMessageAttendant(EntradaMensagemDTO entradaMensagemDTO, String channelId, String attendantId) throws Exception {

        //getIdentificadorRemetente retorna o ID da Sessão do Usuário setado no Cotroler
        SessaoEntity sessaoEntity = sessaoService.getSessionById(entradaMensagemDTO.getIdentificadorRemetente());
        LocalDateTime now = LocalDateTime.now();
        try{
            sessaoService.verifyWindowToWhatsappChannel(sessaoEntity, now);

            DialogoDTO dialogoDTO = DialogoDTO.builder()
                    .mensagem(formatAnswearToClient(entradaMensagemDTO, attendantId))
                    .identificadorRemetente(String.valueOf(sessaoEntity.getIdentificadorUsuario()))
                    .toIdentifier(sessaoEntity.getId())
                    .canal(sessaoEntity.getCanal())
                    .type(entradaMensagemDTO.getType())
                    .media(mountMediaSendFile(entradaMensagemDTO.getFile(), entradaMensagemDTO.getType(), channelId))
                    .from(AssistentEnum.ATTENDANT.name())
                    .channelUuid(channelId)
                    .sessionId(sessaoEntity.getId())
                    .createdDateTime(LocalDateTime.now())
                    .build();


            DialogoDTO dialogoDTO1 = forwardService.enviaRespostaDialogoPorCanal(entradaMensagemDTO.getCanal(), dialogoDTO);
            attendantAssistent.processaDialogoAssistent(dialogoDTO1);

        } catch (IllegalArgumentException e) {
            sessaoEntity.setAtendimentoAberto(Boolean.FALSE);
            sessaoService.salva(sessaoEntity);

            log.warn("Janela fechada para o atendente {}",attendantId);
            simpMessagingTemplate.convertAndSend(String.format(WebsocketPath.Constants.REALOAD, attendantId),
                    true);
        }



    }

    private MediaDTO mountMediaSendFile(MultipartFile file, String type, String channelId) throws NoSuchAlgorithmException, IOException, ServerException, InsufficientDataException, ErrorResponseException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        MediaDTO media = null;

        if(file != null){
            media = MediaDTO.builder()
                    .id(generateFileHash(file))
                    .mimeType(file.getContentType())
                    .mediaType(type)
                    .build();
            minioService.putFile(file, media.getId(), channelId);
        }

        return media;
    }
    public  String generateFileHash(MultipartFile file) throws   NoSuchAlgorithmException, IOException {
        // Escolha o algoritmo de hash, como MD5 ou SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = file.getBytes();

        // Calcula o hash
        byte[] hashBytes = digest.digest(fileBytes);

        // Converte o hash em uma string hexadecimal
        BigInteger bigInt = new BigInteger(1, hashBytes);
        return bigInt.toString(16); // Retorna o hash como uma string
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