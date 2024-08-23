package com.cbio.chat.repositories;

import com.cbio.chat.models.ChatChannelEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatChannelRepository extends MongoRepository<ChatChannelEntity, String> {
//  @Query(" FROM"
//      + "    ChatChannel c"
//      + "  WHERE"
//      + "    c.userOne.id IN (:userOneId, :userTwoId) "
//      + "  AND"
//      + "    c.userTwo.id IN (:userOneId, :userTwoId)")
//  public List<ChatChannel> findExistingChannel(
//      @Param("userOneId") long userOneId, @Param("userTwoId") long userTwoId);

  @Query(value="{'userOne.uuid' :  ?0, 'userTwo.uuid' :  ?1}", fields="{'id' : 1, 'userOne' : 1, 'userTwo' : 1}")
  List<ChatChannelEntity> findByUserOneUuidAndUserTwoUuid(String userOneId, String userTwoId);

  @Query(" SELECT"
      + "    uuid"
      + "  FROM"
      + "    ChatChannel c"
      + "  WHERE"
      + "    c.userOne.id IN (:userIdOne, :userIdTwo)"
      + "  AND"
      + "    c.userTwo.id IN (:userIdOne, :userIdTwo)")
  public String getChannelUuid(
      @Param("userIdOne") long userIdOne, @Param("userIdTwo") long userIdTwo);

  @Query(" FROM"
      + "    ChatChannel c"
      + "  WHERE"
      + "    c.uuid IS :uuid")
  public ChatChannelEntity getChannelDetails(@Param("uuid") String uuid);
}