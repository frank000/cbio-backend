package com.cbio.chat.strategies;

import com.cbio.chat.models.UserChatEntity;
import com.cbio.chat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;

@Service
public class UserRetrievalBySecurityContextStrategy implements IUserRetrievalStrategy<SecurityContext> {
  private UserRepository userRepository;

  @Autowired
  public UserRetrievalBySecurityContextStrategy(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserChatEntity getUser(SecurityContext securityContext) {


    return null;
  }

  @Override
  public UserChatEntity getUserByUsername(SecurityContext userIdentifier) {
    return null;
  }
}