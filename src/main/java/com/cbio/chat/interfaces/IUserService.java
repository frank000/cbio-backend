package com.cbio.chat.interfaces;

import com.cbio.chat.dto.NotificationDTO;
import com.cbio.chat.dto.RegistrationDTO;
import com.cbio.chat.dto.UserDTO;
import com.cbio.chat.exceptions.UserNotFoundException;
import com.cbio.chat.models.UserChatEntity;
import jakarta.validation.ValidationException;
import org.springframework.beans.BeansException;
import org.springframework.security.core.context.SecurityContext;

import java.util.List;

public interface IUserService {
  UserChatEntity getUserByUsername(String username)
      throws BeansException, UserNotFoundException;

  UserChatEntity getUser(String userId)
      throws BeansException, UserNotFoundException;

  UserChatEntity getUser(SecurityContext securityContext)
      throws BeansException, UserNotFoundException;

  boolean doesUserExist(String email);

  void addUser(RegistrationDTO registrationDTO)
      throws ValidationException;

  List<UserDTO> retrieveFriendsList(UserChatEntity user);

  UserDTO retrieveUserInfo(UserChatEntity user);

  void setIsPresent(UserChatEntity user, Boolean stat);

  Boolean isPresent(UserChatEntity user);

  void notifyUser(UserChatEntity user, NotificationDTO notification);
}