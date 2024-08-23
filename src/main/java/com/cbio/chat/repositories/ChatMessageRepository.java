package com.cbio.chat.repositories;

import com.cbio.chat.models.ChatMessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessageEntity, String> {
//    @Query(" FROM"
//        + "    ChatMessage m"
//        + "  WHERE"
//        + "    m.authorUser.id IN (:userIdOne, :userIdTwo)"
//        + "  AND"
//        + "    m.recipientUser.id IN (:userIdOne, :userIdTwo)"
//        + "  ORDER BY"
//        + "    m.timeSent"
//        + "  DESC")
//    public List<ChatMessage> getExistingChatMessages(
//        @Param("userIdOne") long userIdOne, @Param("userIdTwo") long userIdTwo, Pageable pageable);

    @Query(value = "{authorUser.id :  ?0, recipientUser.id :  ?1}", fields ="{id: 1, authorUser: 1, recipientUser: 1,  timeSent:  1, contents: 1 }" )
    List<ChatMessageEntity> getExistingChatMessages(String authorUserId, String recipientUserId, Pageable pageable);
}
