package com.cbio.chat.strategies;

import com.cbio.chat.models.UserChatEntity;

public interface IUserRetrievalStrategy<T> {
  public UserChatEntity getUser(T userIdentifier);
  public UserChatEntity getUserByUsername(T userIdentifier);
}