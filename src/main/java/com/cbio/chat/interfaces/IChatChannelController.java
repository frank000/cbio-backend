package com.cbio.chat.interfaces;

import com.cbio.chat.dto.ChatChannelInitializationDTO;
import com.cbio.chat.dto.ChatMessageDTO;
import com.cbio.chat.exceptions.IsSameUserException;
import com.cbio.chat.exceptions.UserNotFoundException;
import org.springframework.beans.BeansException;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface IChatChannelController {
    ChatMessageDTO chatMessage(@DestinationVariable String channelId, ChatMessageDTO message)
        throws BeansException, UserNotFoundException;

    ResponseEntity<String> establishChatChannel(@RequestBody ChatChannelInitializationDTO chatChannelInitialization)
        throws IsSameUserException, UserNotFoundException;

    ResponseEntity<String> getExistingChatMessages(@PathVariable("channelUuid") String channelUuid);
}
