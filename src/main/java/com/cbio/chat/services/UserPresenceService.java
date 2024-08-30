package com.cbio.chat.services;
import com.cbio.chat.exceptions.UserNotFoundException;
import com.cbio.chat.interfaces.IUserPresenceService;
import com.cbio.chat.models.UserChatEntity;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class UserPresenceService implements IUserPresenceService, ChannelInterceptor {
  @Autowired 
  private UserChatService userService;

  @Override
  public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
    StompHeaderAccessor stompDetails = StompHeaderAccessor.wrap(message);

    if(stompDetails.getCommand() == null) { return; }

    switch(stompDetails.getCommand()) {
      case CONNECT:    
      case CONNECTED:
        toggleUserPresence(stompDetails.getUser().getName().toString(), true);
        break;
      case DISCONNECT:
        toggleUserPresence(stompDetails.getUser().getName().toString(), false);
        break;
      default:
        break;
    }
  }

  private void toggleUserPresence(String userEmail, Boolean isPresent) {
    try {
      UserChatEntity user = userService.getUser(userEmail);
      userService.setIsPresent(user, isPresent);
    } catch (BeansException | UserNotFoundException e) {
      e.printStackTrace();
    }
  }
}