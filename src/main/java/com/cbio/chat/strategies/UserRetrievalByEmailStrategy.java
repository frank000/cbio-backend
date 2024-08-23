package com.cbio.chat.strategies;

import com.cbio.chat.models.UserChatEntity;
import com.cbio.chat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRetrievalByEmailStrategy implements IUserRetrievalStrategy<String> {
  private UserRepository userRepository;

  @Autowired
  public UserRetrievalByEmailStrategy(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserChatEntity getUser(String userIdentifier) {
    return userRepository.findByUsername(userIdentifier);
  }

  @Override
  public UserChatEntity getUserByUsername(String userIdentifier) {
    return userRepository.findByUsername(userIdentifier);
  }
}