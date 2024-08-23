package com.cbio.chat.repositories;

import com.cbio.chat.models.ChatChannelEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatChannelCustomRepository  {
  private final MongoTemplate mongoTemplate;

  public List<ChatChannelEntity> findUsers(String userOneUuid, String userTwoUuid) {
    Query query = new Query();
    query.addCriteria(Criteria.where("userOne.uuid").is(userOneUuid)
            .and("userTwo.uuid").is(userTwoUuid));
    query.fields().include("id").include("userOne").include("userTwo");

    return mongoTemplate.find(query, ChatChannelEntity.class);
  }
}