package com.cbio.chat.repositories;

import com.cbio.chat.models.ChatChannelEntity;
import com.cbio.chat.models.DialogoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DialogoRepository extends MongoRepository<DialogoEntity, String> {

}