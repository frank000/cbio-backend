package com.cbio.chat.repositories;

import com.cbio.chat.models.UserChatEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface UserRepository extends MongoRepository<UserChatEntity, String> {

  UserChatEntity findByUsername(String username);

  Optional<UserChatEntity> findById(String id);

  Optional<UserChatEntity> findByUuid(String id);


//  @org.springframework.data.mongodb.repository.Query(" FROM"
//      + "    User u"
//      + "  WHERE"
//      + "    u.email IS NOT :excludedUser")
//  public List<UserChatEntity> findFriendsListFor(@Param("excludedUser") String excludedUser);
}